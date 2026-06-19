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
        lowStockProduct    = new Product(1, "111", "Widget A", 9.99,  3,  5, true);
        normalStockProduct = new Product(2, "222", "Widget B", 19.99, 20, 5, true);
        atThresholdProduct = new Product(3, "333", "Widget C", 4.99,  5,  5, true);
    }
    @Test
    public void testIsLowStock_WhenBelowThreshold_ReturnsTrue() {
        assertTrue(lowStockProduct.isLowStock(),
            "isLowStock() should return true when stockQuantity is below lowThreshold");
    }
    @Test
    public void testIsLowStock_WhenAboveThreshold_ReturnsFalse() {
        assertFalse(normalStockProduct.isLowStock(),
            "isLowStock() should return false when stockQuantity is above lowThreshold");
    }
    @Test
    public void testIsLowStock_WhenAtThreshold_ReturnsTrue() {
        assertTrue(atThresholdProduct.isLowStock(),
            "isLowStock() should return true when stockQuantity equals lowThreshold");
    }
    @Test
    public void testGetDisplayPrice_FormatsCorrectly() {
        assertEquals("$9.99", lowStockProduct.getDisplayPrice(),
            "getDisplayPrice() should return price formatted as $X.XX");
    }
    @Test
    public void testSetPrice_RoundsToTwoDecimals() {
        normalStockProduct.setPrice(14.999);
        assertEquals(15.00, normalStockProduct.getPrice(), 0.001,
            "setPrice() should round to 2 decimal places");
    }
}