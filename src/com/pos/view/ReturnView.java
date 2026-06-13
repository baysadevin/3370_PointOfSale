package com.pos.view;

import com.pos.dao.ProductDAO;
import com.pos.dao.TransactionDAO;
import com.pos.model.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ReturnView {
    private final User currentUser;
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final ObservableList<TransactionItem> foundItems = FXCollections.observableArrayList();
    private Label infoLabel;
    private Label messageLabel;
    private Transaction foundTransaction;

    public ReturnView(User user) {
        this.currentUser = user;
    }

    @SuppressWarnings("unchecked")
    public Node getView() {
        TextField txnIdField = new TextField();
        txnIdField.setPromptText("Transaction ID");
        Button lookupBtn = new Button("Look Up");

        infoLabel = new Label("");
        infoLabel.setWrapText(true);

        TableView<TransactionItem> table = new TableView<>(foundItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TransactionItem, Integer> idCol = new TableColumn<>("Product ID");
        idCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getProductId()).asObject());

        TableColumn<TransactionItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()).asObject());

        TableColumn<TransactionItem, String> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getUnitPrice())));

        TableColumn<TransactionItem, String> totalCol = new TableColumn<>("Line Total");
        totalCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getLineTotal())));

        table.getColumns().addAll(idCol, qtyCol, priceCol, totalCol);

        Button confirmBtn = new Button("Confirm Return");
        confirmBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 20;");
        confirmBtn.setDisable(true);

        messageLabel = new Label("");
        messageLabel.setWrapText(true);

        lookupBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txnIdField.getText().trim());
                foundTransaction = transactionDAO.findById(id);
                foundItems.clear();
                if (foundTransaction == null || foundTransaction.getType().equals("RETURN")) {
                    infoLabel.setText("Transaction not found or already returned.");
                    confirmBtn.setDisable(true);
                } else {
                    List<TransactionItem> items = transactionDAO.getItemsByTransactionId(id);
                    foundItems.addAll(items);
                    infoLabel.setText(String.format("Transaction #%d  |  %s  |  Total: $%.2f",
                        foundTransaction.getId(), foundTransaction.getPaymentMethod(), foundTransaction.getTotal()));
                    confirmBtn.setDisable(false);
                }
            } catch (NumberFormatException ex) {
                infoLabel.setText("Enter a valid transaction ID.");
            }
        });

        confirmBtn.setOnAction(e -> {
            if (foundTransaction == null) return;
            int returnId = transactionDAO.saveReturn(foundTransaction.getId(), currentUser.getEmployeeID());
            if (returnId > 0) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText(String.format("Return complete! Return Transaction #%d  |  Refund: $%.2f",
                    returnId, foundTransaction.getTotal()));
                foundItems.clear();
                infoLabel.setText("");
                confirmBtn.setDisable(true);
                foundTransaction = null;
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Return failed. Please try again.");
            }
        });

        HBox lookupRow = new HBox(8, new Label("Transaction ID:"), txnIdField, lookupBtn);

        VBox content = new VBox(12, lookupRow, infoLabel, table, confirmBtn, messageLabel);
        content.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        return content;
    }
}