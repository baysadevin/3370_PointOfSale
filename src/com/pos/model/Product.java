package com.pos.model;

public class Product {
    private int id;
    private String barcode;
    private String name;
    private double price;
    private int stockQuantity;
    private int lowThreshold;
    private boolean active;

        public Product(int id, String barcode, String name, double price, int stockQuantity, int lowThreshold, boolean active) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.lowThreshold = lowThreshold;
        this.active = active;
    }

        public int getId() { return id; }
        public String getBarcode() { return barcode; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getStockQuantity() { return stockQuantity; }
        public int getLowThreshold() { return lowThreshold; }
        public boolean isActive() { return active; }

        public void setBarcode(String barcode) { this.barcode = barcode; }
        public void setName(String name) { this.name = name; }
        public void setPrice(double price) { this.price = Math.round(price*100.0)/100.0; }
        public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
        public void setLowThreshold(int lowThreshold) { this.lowThreshold = lowThreshold; }
        public void setActive(boolean active) { this.active = active; }

        public boolean isLowStock(){

            return stockQuantity <= lowThreshold;

        }
        public String getDisplayPrice(){

            return String.format("$%.2f", price);

        }
    }
