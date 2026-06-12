package com.pos.test;

import com.pos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    private Product lowStockProduct;
    private Product normalStockProduct;
    private Product atThresholdProduct;

    @BeforeEach
    public void setUp() {
        // stock(3) is below threshold(5) — low stock
        lowStockProduct    = new Product(1, "111", "Widget A", 9.99,  3,  5, true);
        // stock(20) is above threshold(5) — normal stock
        normalStockProduct = new Product(2, "222", "Widget B", 19.99, 20, 5, true);
        // stock(5) equals threshold(5) — still considered low stock
        atThresholdProduct = new Product(3, "333", "Widget C", 4.99,  5,  5, true);
    }

    // Test 1: isLowStock() returns true when stock is below threshold
    @Test
    public void testIsLowStock_WhenBelowThreshold_ReturnsTrue() {
        assertTrue(lowStockProduct.isLowStock(),
            "isLowStock() should return true when stockQuantity is below lowThreshold");
    }

    // Test 2: isLowStock() returns false when stock is above threshold
    @Test
    public void testIsLowStock_WhenAboveThreshold_ReturnsFalse() {
        assertFalse(normalStockProduct.isLowStock(),
            "isLowStock() should return false when stockQuantity is above lowThreshold");
    }

    // Test 3: isLowStock() returns true when stock equals threshold (edge case)
    @Test
    public void testIsLowStock_WhenAtThreshold_ReturnsTrue() {
        assertTrue(atThresholdProduct.isLowStock(),
            "isLowStock() should return true when stockQuantity equals lowThreshold");
    }

    // Test 4: getDisplayPrice() formats price with dollar sign and 2 decimal places
    @Test
    public void testGetDisplayPrice_FormatsCorrectly() {
        assertEquals("$9.99", lowStockProduct.getDisplayPrice(),
            "getDisplayPrice() should return price formatted as $X.XX");
    }

    // Test 5: setPrice() rounds to 2 decimal places correctly
    @Test
    public void testSetPrice_RoundsToTwoDecimals() {
        normalStockProduct.setPrice(14.999);
        assertEquals(15.00, normalStockProduct.getPrice(), 0.001,
            "setPrice() should round to 2 decimal places");
    }
}