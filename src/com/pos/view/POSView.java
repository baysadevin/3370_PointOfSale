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

import java.util.ArrayList;
import java.util.List;

public class POSView {
    private final User currentUser;
    private final ProductDAO productDAO = new ProductDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private final double TAX_RATE = 0.06;

    private Label subtotalLabel, taxLabel, totalLabel, changeLabel, messageLabel;
    private TextField barcodeField, tenderedField, searchField;
    private ComboBox<String> paymentCombo;
    private TableView<Product> productTable;

    public POSView(User user) {
        this.currentUser = user;
    }

    @SuppressWarnings("unchecked")
    public Node getView() {
        // ---- Product Reference Panel (left) ----
        searchField = new TextField();
        searchField.setPromptText("Search items...");
        searchField.textProperty().addListener((obs, old, val) -> filterProducts(val));

        productTable = new TableView<>(allProducts);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productTable.setPrefWidth(220);

        TableColumn<Product, String> pNameCol = new TableColumn<>("Name");
        pNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<Product, String> pBarcodeCol = new TableColumn<>("Barcode");
        pBarcodeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBarcode()));

        productTable.getColumns().addAll(pNameCol, pBarcodeCol);
        productTable.setOnMouseClicked(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                barcodeField.setText(selected.getBarcode());
                barcodeField.requestFocus();
            }
        });

        VBox productPanel = new VBox(6, new Label("Store Items"), searchField, productTable);
        productPanel.setPadding(new Insets(12));
        productPanel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dddddd; -fx-border-width: 0 1 0 0;");
        productPanel.setPrefWidth(230);
        VBox.setVgrow(productTable, Priority.ALWAYS);

        loadProducts();

        // ---- Cart (center) ----
        barcodeField = new TextField();
        barcodeField.setPromptText("Scan or enter barcode");
        Button addBtn = new Button("Add Item");
        addBtn.setOnAction(e -> addItem());
        barcodeField.setOnAction(e -> addItem());

        HBox barcodeRow = new HBox(8, new Label("Barcode:"), barcodeField, addBtn);
        barcodeRow.setPadding(new Insets(0, 0, 8, 0));

        TableView<CartItem> cartTable = buildCartTable();

        // ---- Totals & Payment (right) ----
        subtotalLabel = new Label("Subtotal: $0.00");
        taxLabel      = new Label("Tax (6%): $0.00");
        totalLabel    = new Label("Total:    $0.00");
        totalLabel.setStyle("-fx-font-weight: bold;");
        changeLabel   = new Label("Change:   $0.00");

        paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll("CASH", "CARD", "GIFT_CARD");
        paymentCombo.setValue("CASH");

        tenderedField = new TextField();
        tenderedField.setPromptText("Amount tendered");
        paymentCombo.setOnAction(e ->
            tenderedField.setDisable(!paymentCombo.getValue().equals("CASH")));

        Button processBtn = new Button("Process Sale");
        processBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20;");
        processBtn.setOnAction(e -> processSale());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 20;");
        clearBtn.setOnAction(e -> clearCart());

        messageLabel = new Label("");
        messageLabel.setWrapText(true);

        VBox right = new VBox(8,
            new Separator(),
            subtotalLabel, taxLabel, totalLabel,
            new Separator(),
            new Label("Payment Method:"), paymentCombo,
            new Label("Amount Tendered:"), tenderedField,
            changeLabel,
            new Separator(),
            processBtn, clearBtn, messageLabel);
        right.setPadding(new Insets(8));
        right.setPrefWidth(240);

        VBox center = new VBox(8, barcodeRow, cartTable);
        center.setPadding(new Insets(16));
        VBox.setVgrow(cartTable, Priority.ALWAYS);

        HBox content = new HBox(productPanel, center, right);
        HBox.setHgrow(center, Priority.ALWAYS);
        return content;
    }

    private void loadProducts() {
        allProducts.clear();
        allProducts.addAll(productDAO.findAll());
    }

    private void filterProducts(String query) {
        allProducts.clear();
        productDAO.findAll().stream()
            .filter(p -> query.isEmpty() ||
                p.getName().toLowerCase().contains(query.toLowerCase()) ||
                p.getBarcode().contains(query))
            .forEach(allProducts::add);
    }

    @SuppressWarnings("unchecked")
    private TableView<CartItem> buildCartTable() {
        TableView<CartItem> table = new TableView<>(cartItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProduct().getName()));

        TableColumn<CartItem, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProduct().getBarcode()));

        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()).asObject());

        TableColumn<CartItem, String> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProduct().getDisplayPrice()));

        TableColumn<CartItem, String> totalCol = new TableColumn<>("Line Total");
        totalCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getLineTotal())));

        table.getColumns().addAll(nameCol, barcodeCol, qtyCol, priceCol, totalCol);
        return table;
    }

    private void addItem() {
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) return;

        Product product = productDAO.findByBarcode(barcode);
        if (product == null) { showMessage("Product not found: " + barcode, true); return; }
        if (product.getStockQuantity() <= 0) { showMessage("Item out of stock.", true); return; }

        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                cartItems.set(cartItems.indexOf(item), item);
                barcodeField.clear();
                updateTotals();
                return;
            }
        }

        cartItems.add(new CartItem(product, 1));
        barcodeField.clear();
        updateTotals();
    }

    private void updateTotals() {
        double subtotal = Math.round(cartItems.stream().mapToDouble(CartItem::getLineTotal).sum() * 100.0) / 100.0;
        double tax      = Math.round(subtotal * TAX_RATE * 100.0) / 100.0;
        double total    = Math.round((subtotal + tax) * 100.0) / 100.0;
        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
        taxLabel.setText(String.format("Tax (6%%): $%.2f", tax));
        totalLabel.setText(String.format("Total:    $%.2f", total));
    }

    private void processSale() {
        if (cartItems.isEmpty()) { showMessage("Cart is empty.", true); return; }

        double subtotal = Math.round(cartItems.stream().mapToDouble(CartItem::getLineTotal).sum() * 100.0) / 100.0;
        double tax      = Math.round(subtotal * TAX_RATE * 100.0) / 100.0;
        double total    = Math.round((subtotal + tax) * 100.0) / 100.0;
        double tendered = 0, change = 0;

        if (paymentCombo.getValue().equals("CASH")) {
            try { tendered = Double.parseDouble(tenderedField.getText().trim()); }
            catch (NumberFormatException e) { showMessage("Enter a valid tendered amount.", true); return; }
            if (tendered < total) { showMessage("Insufficient payment.", true); return; }
            change = Math.round(Math.round((tendered - total) / 0.05) * 0.05 * 100.0) / 100.0;
            changeLabel.setText(String.format("Change:   $%.2f", change));
        }

        Transaction t = new Transaction(0, currentUser.getEmployeeID(), "SALE",
            paymentCombo.getValue(), subtotal, tax, total, tendered, change, "", false);

        int id = transactionDAO.saveSale(t, new ArrayList<>(cartItems));
        if (id > 0) {
            showMessage("Sale complete! Transaction #" + id +
                (paymentCombo.getValue().equals("CASH") ? String.format("  Change: $%.2f", change) : ""), false);
            clearCart();
        } else {
            showMessage("Sale failed. Please try again.", true);
        }
    }

    private void clearCart() {
        cartItems.clear();
        subtotalLabel.setText("Subtotal: $0.00");
        taxLabel.setText("Tax (6%): $0.00");
        totalLabel.setText("Total:    $0.00");
        changeLabel.setText("Change:   $0.00");
        tenderedField.clear();
        barcodeField.clear();
    }

    private void showMessage(String msg, boolean error) {
        messageLabel.setStyle(error ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        messageLabel.setText(msg);
    }
}