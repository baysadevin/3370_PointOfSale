package com.pos.dao;

import com.pos.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public Product findByBarcode(String barcode) {
        String sql = "SELECT * FROM products WHERE barcode = ? AND active = 1";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("ProductDAO.findByBarcode: " + e.getMessage());
        }
        return null;
    }

    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE active = 1";
        try (Statement stmt = db.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("ProductDAO.findAll: " + e.getMessage());
        }
        return list;
    }

    public boolean create(Product p) {
        String sql = "INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES (?, ?, ?, ?, ?, 1)";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.getBarcode());
            stmt.setString(2, p.getName());
            stmt.setDouble(3, p.getPrice());
            stmt.setInt(4, p.getStockQuantity());
            stmt.setInt(5, p.getLowThreshold());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProductDAO.create: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Product p) {
        String sql = "UPDATE products SET barcode = ?, name = ?, price = ?, stockQuantity = ?, lowThreshold = ? WHERE id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.getBarcode());
            stmt.setString(2, p.getName());
            stmt.setDouble(3, p.getPrice());
            stmt.setInt(4, p.getStockQuantity());
            stmt.setInt(5, p.getLowThreshold());
            stmt.setInt(6, p.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProductDAO.update: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "UPDATE products SET active = 0 WHERE id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProductDAO.delete: " + e.getMessage());
            return false;
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("id"),
            rs.getString("barcode"),
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getInt("stockQuantity"),
            rs.getInt("lowThreshold"),
            rs.getInt("active") == 1
        );
    }
}