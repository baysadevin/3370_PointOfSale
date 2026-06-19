package com.pos.view;

import com.pos.dao.ProductDAO;
import com.pos.dao.TransactionDAO;
import com.pos.model.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReturnView {
    private final User currentUser;
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final ObservableList<ItemRow> foundItems = FXCollections.observableArrayList();
    private Label infoLabel, messageLabel;
    private Button confirmBtn;
    private Transaction selectedTransaction;
    private TableView<ItemRow> itemTable;

    private static class ItemRow {
        final TransactionItem item;
        final String productName;
        final String barcode;
        ItemRow(TransactionItem item, String name, String barcode) {
            this.item = item;
            this.productName = name;
            this.barcode = barcode;
        }
    }

    public ReturnView(User user) {
        this.currentUser = user;
    }

    @SuppressWarnings("unchecked")
    public Node getView() {
        Label txnLabel = new Label("Recent Sales — click to select");
        txnLabel.setStyle("-fx-font-weight: bold;");

        TableView<Transaction> txnTable = new TableView<>(transactions);
        txnTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("Txn #");
        idCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCreatedAt()));

        TableColumn<Transaction, String> empCol = new TableColumn<>("Employee");
        empCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmployeeID()));

        TableColumn<Transaction, String> payCol = new TableColumn<>("Payment");
        payCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPaymentMethod()));

        TableColumn<Transaction, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getTotal())));

        txnTable.getColumns().addAll(idCol, dateCol, empCol, payCol, totalCol);

        Label itemLabel = new Label("Items — Ctrl+click to select multiple");
        itemLabel.setStyle("-fx-font-weight: bold;");

        itemTable = new TableView<>(foundItems);
        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        itemTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        itemTable.setMaxHeight(180);

        TableColumn<ItemRow, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().productName));

        TableColumn<ItemRow, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().barcode));

        TableColumn<ItemRow, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().item.getQuantity()).asObject());

        TableColumn<ItemRow, String> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().item.getUnitPrice())));

        TableColumn<ItemRow, String> lineTotalCol = new TableColumn<>("Line Total");
        lineTotalCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().item.getLineTotal())));

        itemTable.getColumns().addAll(nameCol, barcodeCol, qtyCol, priceCol, lineTotalCol);

        infoLabel = new Label("Click a transaction above to see its items.");
        infoLabel.setWrapText(true);

        confirmBtn = new Button("Return Selected Items");
        confirmBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 20;");
        confirmBtn.setDisable(true);
        confirmBtn.setOnAction(e -> processReturn());

        messageLabel = new Label("");
        messageLabel.setWrapText(true);

        txnTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedTransaction = selected;
                foundItems.clear();
                List<TransactionItem> items = transactionDAO.getItemsByTransactionId(selected.getId());
                for (TransactionItem item : items) {
                    Product p = productDAO.findById(item.getProductId());
                    String name    = p != null ? p.getName()    : "Unknown";
                    String barcode = p != null ? p.getBarcode() : "N/A";
                    foundItems.add(new ItemRow(item, name, barcode));
                }
                infoLabel.setText(String.format("Transaction #%d  |  %s  |  %s  |  Total: $%.2f",
                    selected.getId(), selected.getCreatedAt(), selected.getPaymentMethod(), selected.getTotal()));
                confirmBtn.setDisable(false);
                messageLabel.setText("");
            }
        });

        loadTransactions();

        VBox content = new VBox(8, txnLabel, txnTable, itemLabel, itemTable, infoLabel, confirmBtn, messageLabel);
        content.setPadding(new Insets(16));
        VBox.setVgrow(txnTable, Priority.ALWAYS);
        return content;
    }

    private void loadTransactions() {
        transactions.clear();
        transactionDAO.findAll().stream()
            .filter(t -> t.getType().equals("SALE") && !t.isReturned())
            .forEach(transactions::add);
    }

    private void processReturn() {
        if (selectedTransaction == null) return;

        List<TransactionItem> selectedItems = itemTable.getSelectionModel()
            .getSelectedItems().stream()
            .map(row -> row.item)
            .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Select at least one item to return.");
            return;
        }

        double refundSubtotal = selectedItems.stream().mapToDouble(TransactionItem::getLineTotal).sum();
        refundSubtotal = Math.round(refundSubtotal * 100.0) / 100.0;
        double refundTotal = Math.round((refundSubtotal + Math.round(refundSubtotal * 0.06 * 100.0) / 100.0) * 100.0) / 100.0;

        int returnId = transactionDAO.processPartialReturn(selectedTransaction.getId(), selectedItems, currentUser.getEmployeeID());
        if (returnId > 0) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText(String.format("Return complete! Return Txn #%d  |  Refund: $%.2f", returnId, refundTotal));
            foundItems.clear();
            infoLabel.setText("Click a transaction above to see its items.");
            confirmBtn.setDisable(true);
            selectedTransaction = null;
            loadTransactions();
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Return failed. Please try again.");
        }
    }
}