package com.pos.view;

import com.pos.dao.TransactionDAO;
import com.pos.model.Transaction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ReportView {
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private Label summaryLabel;

    @SuppressWarnings("unchecked")
    public Node getView() {
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("ALL", "SALE", "RETURN");
        typeCombo.setValue("ALL");

        Button generateBtn = new Button("Generate Report");
        summaryLabel = new Label("");

        TableView<Transaction> table = new TableView<>(transactions);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCreatedAt()));

        TableColumn<Transaction, String> empCol = new TableColumn<>("Employee");
        empCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmployeeID()));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType()));

        TableColumn<Transaction, String> payCol = new TableColumn<>("Payment");
        payCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPaymentMethod()));

        TableColumn<Transaction, String> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getSubtotal())));

        TableColumn<Transaction, String> taxCol = new TableColumn<>("Tax");
        taxCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getTaxAmount())));

        TableColumn<Transaction, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getTotal())));

        table.getColumns().addAll(idCol, dateCol, empCol, typeCol, payCol, subtotalCol, taxCol, totalCol);

        generateBtn.setOnAction(e -> {
            transactions.clear();
            List<Transaction> results = typeCombo.getValue().equals("ALL")
                ? transactionDAO.findAll()
                : transactionDAO.findByType(typeCombo.getValue());
            transactions.addAll(results);

            double total = results.stream().mapToDouble(Transaction::getTotal).sum();
            summaryLabel.setText(String.format("Transactions: %d   |   Total Revenue: $%.2f",
                results.size(), total));
        });

        HBox controls = new HBox(8, new Label("Filter:"), typeCombo, generateBtn);
        VBox content = new VBox(10, controls, table, summaryLabel);
        content.setPadding(new Insets(16));
        VBox.setVgrow(table, Priority.ALWAYS);
        return content;
    }
}