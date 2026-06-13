package com.pos.view;

import com.pos.dao.ProductDAO;
import com.pos.model.Product;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class InventoryView {
    private final ProductDAO productDAO = new ProductDAO();
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private TextField barcodeField, nameField, priceField, stockField, thresholdField;
    private Label messageLabel;
    private TableView<Product> table;

    @SuppressWarnings("unchecked")
    public Node getView() {
        table = new TableView<>(products);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());

        TableColumn<Product, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBarcode()));

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<Product, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDisplayPrice()));

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getStockQuantity()).asObject());

        TableColumn<Product, Integer> threshCol = new TableColumn<>("Threshold");
        threshCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getLowThreshold()).asObject());

        TableColumn<Product, String> alertCol = new TableColumn<>("Alert");
        alertCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isLowStock() ? "LOW STOCK" : ""));

        table.getColumns().addAll(idCol, barcodeCol, nameCol, priceCol, stockCol, threshCol, alertCol);

        barcodeField    = new TextField(); barcodeField.setPromptText("Barcode");
        nameField       = new TextField(); nameField.setPromptText("Name");
        priceField      = new TextField(); priceField.setPromptText("Price");
        stockField      = new TextField(); stockField.setPromptText("Stock");
        thresholdField  = new TextField(); thresholdField.setPromptText("Threshold");

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                barcodeField.setText(selected.getBarcode());
                nameField.setText(selected.getName());
                priceField.setText(String.valueOf(selected.getPrice()));
                stockField.setText(String.valueOf(selected.getStockQuantity()));
                thresholdField.setText(String.valueOf(selected.getLowThreshold()));
            }
        });

        Button addBtn    = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button refreshBtn = new Button("Refresh");

        addBtn.setOnAction(e -> addProduct());
        updateBtn.setOnAction(e -> updateProduct());
        deleteBtn.setOnAction(e -> deleteProduct());
        refreshBtn.setOnAction(e -> loadProducts());

        messageLabel = new Label("");
        messageLabel.setWrapText(true);

        HBox formRow = new HBox(8, barcodeField, nameField, priceField, stockField, thresholdField,
                                addBtn, updateBtn, deleteBtn, refreshBtn);
        formRow.setPadding(new Insets(8, 0, 0, 0));

        VBox content = new VBox(8, table, formRow, messageLabel);
        content.setPadding(new Insets(16));
        VBox.setVgrow(table, Priority.ALWAYS);

        loadProducts();
        return content;
    }

    private void loadProducts() {
        products.clear();
        products.addAll(productDAO.findAll());
    }

    private void addProduct() {
        try {
            Product p = buildProductFromForm(-1);
            if (productDAO.create(p)) {
                showMessage("Product added.", false);
                clearForm();
                loadProducts();
            } else {
                showMessage("Failed to add product.", true);
            }
        } catch (Exception e) {
            showMessage("Invalid input. Check all fields.", true);
        }
    }

    private void updateProduct() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Select a product to update.", true); return; }
        try {
            Product p = buildProductFromForm(selected.getId());
            if (productDAO.update(p)) {
                showMessage("Product updated.", false);
                loadProducts();
            } else {
                showMessage("Failed to update product.", true);
            }
        } catch (Exception e) {
            showMessage("Invalid input. Check all fields.", true);
        }
    }

    private void deleteProduct() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Select a product to delete.", true); return; }
        if (productDAO.delete(selected.getId())) {
            showMessage("Product removed.", false);
            clearForm();
            loadProducts();
        } else {
            showMessage("Failed to remove product.", true);
        }
    }

    private Product buildProductFromForm(int id) {
        return new Product(
            id,
            barcodeField.getText().trim(),
            nameField.getText().trim(),
            Double.parseDouble(priceField.getText().trim()),
            Integer.parseInt(stockField.getText().trim()),
            Integer.parseInt(thresholdField.getText().trim()),
            true
        );
    }

    private void clearForm() {
        barcodeField.clear(); nameField.clear(); priceField.clear();
        stockField.clear(); thresholdField.clear();
    }

    private void showMessage(String msg, boolean error) {
        messageLabel.setStyle(error ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        messageLabel.setText(msg);
    }
}