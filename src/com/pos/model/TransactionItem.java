package com.pos.model;

public class TransactionItem {
    private int id;
    private int transactionId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double lineTotal;

    public TransactionItem(int id, int transactionId, int productId,
                           int quantity, double unitPrice, double lineTotal) {
        this.id = id;
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public int getId() { return id; }
    public int getTransactionId() { return transactionId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getLineTotal() { return lineTotal; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setLineTotal(double lineTotal) { this.lineTotal = lineTotal; }
}