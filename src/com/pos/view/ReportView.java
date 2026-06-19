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
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import java.util.List;

public class ReportView {
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private Label totalSalesLabel, totalRevenueLabel, totalReturnsLabel, netRevenueLabel, taxLabel;

    @SuppressWarnings("unchecked")
    public Node getView() {
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("ALL", "SALE", "RETURN");
        typeCombo.setValue("ALL");

        DatePicker fromDate = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker toDate   = new DatePicker(LocalDate.now());

        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-padding: 6 14;");

        HBox controls = new HBox(10,
            new Label("Type:"), typeCombo,
            new Label("From:"), fromDate,
            new Label("To:"),   toDate,
            generateBtn);
        controls.setPadding(new Insets(0, 0, 8, 0));

        TableView<Transaction> table = new TableView<>(transactions);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

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

        totalSalesLabel   = new Label("Sales: 0");
        totalRevenueLabel = new Label("Revenue: $0.00");
        totalReturnsLabel = new Label("Returns: 0");
        netRevenueLabel   = new Label("Net Revenue: $0.00");
        taxLabel          = new Label("Tax Collected: $0.00");

        for (Label l : new Label[]{totalSalesLabel, totalRevenueLabel, totalReturnsLabel, taxLabel}) {
            l.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        }
        netRevenueLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        HBox summaryBox = new HBox(24, totalSalesLabel, totalRevenueLabel, totalReturnsLabel, taxLabel, netRevenueLabel);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-background-color: #f0f0f0;");

        generateBtn.setOnAction(e -> {
            String type      = typeCombo.getValue();
            String startDate = fromDate.getValue() != null ? fromDate.getValue().toString() : null;
            String endDate   = toDate.getValue()   != null ? toDate.getValue().toString()   : null;

            List<Transaction> results = transactionDAO.findWithFilters(type, startDate, endDate);
            transactions.clear();
            transactions.addAll(results);

            long salesCount  = results.stream().filter(t -> t.getType().equals("SALE")).count();
            long returnCount = results.stream().filter(t -> t.getType().equals("RETURN")).count();
            double revenue   = results.stream().filter(t -> t.getType().equals("SALE")).mapToDouble(Transaction::getTotal).sum();
            double refunds   = results.stream().filter(t -> t.getType().equals("RETURN")).mapToDouble(Transaction::getTotal).sum();
            double tax       = results.stream().filter(t -> t.getType().equals("SALE")).mapToDouble(Transaction::getTaxAmount).sum();

            totalSalesLabel.setText(String.format("Sales: %d", salesCount));
            totalRevenueLabel.setText(String.format("Revenue: $%.2f", revenue));
            totalReturnsLabel.setText(String.format("Returns: %d", returnCount));
            taxLabel.setText(String.format("Tax Collected: $%.2f", tax));
            netRevenueLabel.setText(String.format("Net Revenue: $%.2f", revenue - refunds));
        });

        generateBtn.fire();

        VBox content = new VBox(8, controls, table, summaryBox);
        content.setPadding(new Insets(16));
        VBox.setVgrow(table, Priority.ALWAYS);
        return content;
    }
}