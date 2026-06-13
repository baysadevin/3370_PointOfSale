package com.pos.model;

public class Transaction {
    private int id;
    private String employeeID;
    private String type;
    private String paymentMethod;
    private double subtotal;
    private double taxAmount;
    private double total;
    private double amountTendered;
    private double changeGiven;
    private String createdAt;
    private boolean returned;

    public Transaction(int id, String employeeID, String type, String paymentMethod,
                       double subtotal, double taxAmount, double total,
                       double amountTendered, double changeGiven, String createdAt, boolean returned) {
        this.id = id;
        this.employeeID = employeeID;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.total = total;
        this.amountTendered = amountTendered;
        this.changeGiven = changeGiven;
        this.createdAt = createdAt;
        this.returned = returned;
    }

    public int getId() { return id; }
    public String getEmployeeID() { return employeeID; }
    public String getType() { return type; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getSubtotal() { return subtotal; }
    public double getTaxAmount() { return taxAmount; }
    public double getTotal() { return total; }
    public double getAmountTendered() { return amountTendered; }
    public double getChangeGiven() { return changeGiven; }
    public String getCreatedAt() { return createdAt; }
    public boolean isReturned() { return returned; }

    public void setType(String type) { this.type = type; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    public void setTotal(double total) { this.total = total; }
    public void setAmountTendered(double amountTendered) { this.amountTendered = amountTendered; }
    public void setChangeGiven(double changeGiven) { this.changeGiven = changeGiven; }
    public void setReturned(boolean returned) { this.returned = returned; }
}