package com.pos.view;

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

public class ReturnView {
    private final User currentUser;
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final ObservableList<TransactionItem> foundItems = FXCollections.observableArrayList();
    private Label infoLabel, messageLabel;
    private Button confirmBtn;
    private Transaction selectedTransaction;

    public ReturnView(User user) {
        this.currentUser = user;
    }

    @SuppressWarnings("unchecked")
    public Node getView() {
        Label txnLabel = new Label("Recent Sales");
        txnLabel.setStyle("-fx-font-weight: bold;");

        TableView<Transaction> txnTable = new TableView<>(transactions);
        txnTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

        Label itemLabel = new Label("Items in Selected Transaction");
        itemLabel.setStyle("-fx-font-weight: bold;");

        TableView<TransactionItem> itemTable = new TableView<>(foundItems);
        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemTable.setMaxHeight(160);

        TableColumn<TransactionItem, Integer> pidCol = new TableColumn<>("Product ID");
        pidCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getProductId()).asObject());

        TableColumn<TransactionItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()).asObject());

        TableColumn<TransactionItem, String> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getUnitPrice())));

        TableColumn<TransactionItem, String> lineTotalCol = new TableColumn<>("Line Total");
        lineTotalCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getLineTotal())));

        itemTable.getColumns().addAll(pidCol, qtyCol, priceCol, lineTotalCol);

        infoLabel = new Label("Click a transaction above to select it.");
        infoLabel.setWrapText(true);

        confirmBtn = new Button("Confirm Return");
        confirmBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 20;");
        confirmBtn.setDisable(true);
        confirmBtn.setOnAction(e -> processReturn());

        messageLabel = new Label("");
        messageLabel.setWrapText(true);

        txnTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedTransaction = selected;
                foundItems.clear();
                foundItems.addAll(transactionDAO.getItemsByTransactionId(selected.getId()));
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
        int returnId = transactionDAO.saveReturn(selectedTransaction.getId(), currentUser.getEmployeeID());
        if (returnId > 0) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText(String.format("Return complete! Return Txn #%d  |  Refund: $%.2f",
                returnId, selectedTransaction.getTotal()));
            foundItems.clear();
            infoLabel.setText("Click a transaction above to select it.");
            confirmBtn.setDisable(true);
            selectedTransaction = null;
            loadTransactions();
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Return failed. Please try again.");
        }
    }
}