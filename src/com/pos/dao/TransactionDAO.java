package com.pos.dao;

import com.pos.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public int saveSale(Transaction t, List<CartItem> items) {
        try {
            Connection conn = db.getConnection();
            conn.createStatement().execute("BEGIN TRANSACTION");

            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO transactions (employeeID, type, payment_method, subtotal, tax_amount, total, amount_tendered, change_given) VALUES (?, 'SALE', ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, t.getEmployeeID());
            stmt.setString(2, t.getPaymentMethod());
            stmt.setDouble(3, t.getSubtotal());
            stmt.setDouble(4, t.getTaxAmount());
            stmt.setDouble(5, t.getTotal());
            stmt.setDouble(6, t.getAmountTendered());
            stmt.setDouble(7, t.getChangeGiven());
            stmt.executeUpdate();

            ResultSet keys = conn.createStatement().executeQuery("SELECT last_insert_rowid()");
            int txnId = keys.next() ? keys.getInt(1) : -1;
            if (txnId == -1) { conn.createStatement().execute("ROLLBACK"); return -1; }

            for (CartItem item : items) {
                PreparedStatement iStmt = conn.prepareStatement(
                    "INSERT INTO transaction_items (transaction_id, product_id, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?)");
                iStmt.setInt(1, txnId);
                iStmt.setInt(2, item.getProduct().getId());
                iStmt.setInt(3, item.getQuantity());
                iStmt.setDouble(4, item.getProduct().getPrice());
                iStmt.setDouble(5, item.getLineTotal());
                iStmt.executeUpdate();

                PreparedStatement sStmt = conn.prepareStatement(
                    "UPDATE products SET stockQuantity = stockQuantity - ? WHERE id = ?");
                sStmt.setInt(1, item.getQuantity());
                sStmt.setInt(2, item.getProduct().getId());
                sStmt.executeUpdate();
            }

            conn.createStatement().execute("COMMIT");
            return txnId;
        } catch (SQLException e) {
            try { db.getConnection().createStatement().execute("ROLLBACK"); } catch (SQLException ex) {}
            System.err.println("TransactionDAO.saveSale: " + e.getMessage());
            return -1;
        }
    }

    public int processPartialReturn(int originalTxnId, List<TransactionItem> selectedItems, String employeeID) {
        Transaction original = findById(originalTxnId);
        if (original == null || selectedItems.isEmpty()) return -1;

        double subtotal = 0;
        for (TransactionItem item : selectedItems) subtotal += item.getLineTotal();
        subtotal = Math.round(subtotal * 100.0) / 100.0;
        double tax = Math.round(subtotal * 0.06 * 100.0) / 100.0;
        double total = Math.round((subtotal + tax) * 100.0) / 100.0;

        try {
            Connection conn = db.getConnection();
            conn.createStatement().execute("BEGIN TRANSACTION");

            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO transactions (employeeID, type, payment_method, subtotal, tax_amount, total) VALUES (?, 'RETURN', ?, ?, ?, ?)");
            stmt.setString(1, employeeID);
            stmt.setString(2, original.getPaymentMethod());
            stmt.setDouble(3, subtotal);
            stmt.setDouble(4, tax);
            stmt.setDouble(5, total);
            stmt.executeUpdate();

            ResultSet rs = conn.createStatement().executeQuery("SELECT last_insert_rowid()");
            int returnId = rs.next() ? rs.getInt(1) : -1;
            if (returnId == -1) { conn.createStatement().execute("ROLLBACK"); return -1; }

            for (TransactionItem item : selectedItems) {
                PreparedStatement iStmt = conn.prepareStatement(
                    "INSERT INTO transaction_items (transaction_id, product_id, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?)");
                iStmt.setInt(1, returnId);
                iStmt.setInt(2, item.getProductId());
                iStmt.setInt(3, item.getQuantity());
                iStmt.setDouble(4, item.getUnitPrice());
                iStmt.setDouble(5, item.getLineTotal());
                iStmt.executeUpdate();

                PreparedStatement sStmt = conn.prepareStatement(
                    "UPDATE products SET stockQuantity = stockQuantity + ? WHERE id = ?");
                sStmt.setInt(1, item.getQuantity());
                sStmt.setInt(2, item.getProductId());
                sStmt.executeUpdate();

                PreparedStatement dStmt = conn.prepareStatement(
                    "DELETE FROM transaction_items WHERE id = ?");
                dStmt.setInt(1, item.getId());
                dStmt.executeUpdate();
            }

            ResultSet remaining = conn.createStatement().executeQuery(
                "SELECT COUNT(*) AS cnt FROM transaction_items WHERE transaction_id = " + originalTxnId);
            int remainingCount = remaining.next() ? remaining.getInt("cnt") : 0;

            if (remainingCount == 0) {
                PreparedStatement markStmt = conn.prepareStatement(
                    "UPDATE transactions SET returned = 1 WHERE id = ?");
                markStmt.setInt(1, originalTxnId);
                markStmt.executeUpdate();
            } else {
                ResultSet totalsRs = conn.createStatement().executeQuery(
                    "SELECT SUM(line_total) AS total FROM transaction_items WHERE transaction_id = " + originalTxnId);
                double newSubtotal = totalsRs.next() ? Math.round(totalsRs.getDouble("total") * 100.0) / 100.0 : 0;
                double newTax = Math.round(newSubtotal * 0.06 * 100.0) / 100.0;
                double newTotal = Math.round((newSubtotal + newTax) * 100.0) / 100.0;

                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE transactions SET subtotal = ?, tax_amount = ?, total = ? WHERE id = ?");
                updateStmt.setDouble(1, newSubtotal);
                updateStmt.setDouble(2, newTax);
                updateStmt.setDouble(3, newTotal);
                updateStmt.setInt(4, originalTxnId);
                updateStmt.executeUpdate();
            }

            conn.createStatement().execute("COMMIT");
            return returnId;
        } catch (SQLException e) {
            try { db.getConnection().createStatement().execute("ROLLBACK"); } catch (SQLException ex) {}
            System.err.println("TransactionDAO.processPartialReturn: " + e.getMessage());
            return -1;
        }
    }

    public Transaction findById(int id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("TransactionDAO.findById: " + e.getMessage());
        }
        return null;
    }

    public List<TransactionItem> getItemsByTransactionId(int transactionId) {
        List<TransactionItem> list = new ArrayList<>();
        String sql = "SELECT * FROM transaction_items WHERE transaction_id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new TransactionItem(
                    rs.getInt("id"),
                    rs.getInt("transaction_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("line_total")
                ));
            }
        } catch (SQLException e) {
            System.err.println("TransactionDAO.getItems: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        try (Statement stmt = db.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM transactions ORDER BY id DESC");
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("TransactionDAO.findAll: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> findWithFilters(String type, String startDate, String endDate) {
        List<Transaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (type != null && !type.equals("ALL")) {
            sql.append(" AND type = ?");
            params.add(type);
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND date(created_at) >= date(?)");
            params.add(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND date(created_at) <= date(?)");
            params.add(endDate);
        }
        sql.append(" ORDER BY id DESC");

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("TransactionDAO.findWithFilters: " + e.getMessage());
        }
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("id"),
            rs.getString("employeeID"),
            rs.getString("type"),
            rs.getString("payment_method"),
            rs.getDouble("subtotal"),
            rs.getDouble("tax_amount"),
            rs.getDouble("total"),
            rs.getDouble("amount_tendered"),
            rs.getDouble("change_given"),
            rs.getString("created_at"),
            rs.getInt("returned") == 1
        );
    }
}