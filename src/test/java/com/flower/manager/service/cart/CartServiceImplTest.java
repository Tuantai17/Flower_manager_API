package com.flower.manager.service.cart;

import com.flower.manager.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests cho Cart - Entity Tests
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Cart Unit Tests")
class CartServiceImplTest {

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = Product.builder()
                .id(1L)
                .name("Bó hoa hồng")
                .slug("bo-hoa-hong")
                .price(new BigDecimal("250000"))
                .salePrice(null)
                .stockQuantity(100)
                .active(true)
                .status(1)
                .build();
    }

    @Test
    @DisplayName("Product isOnSale should return false when no sale price")
    void product_IsOnSale_ReturnsFalseWhenNoSalePrice() {
        assertFalse(testProduct.isOnSale());
    }

    @Test
    @DisplayName("Product isOnSale should return true when sale price is set")
    void product_IsOnSale_ReturnsTrueWhenSalePriceSet() {
        testProduct.setSalePrice(new BigDecimal("200000"));
        assertTrue(testProduct.isOnSale());
    }

    @Test
    @DisplayName("Product getCurrentPrice should return regular price when no sale")
    void product_GetCurrentPrice_ReturnsRegularPriceWhenNoSale() {
        assertEquals(new BigDecimal("250000"), testProduct.getCurrentPrice());
    }

    @Test
    @DisplayName("Product getCurrentPrice should return sale price when on sale")
    void product_GetCurrentPrice_ReturnsSalePriceWhenOnSale() {
        testProduct.setSalePrice(new BigDecimal("200000"));
        assertEquals(new BigDecimal("200000"), testProduct.getCurrentPrice());
    }

    @Test
    @DisplayName("Product isInStock should return true when stock > 0")
    void product_IsInStock_ReturnsTrueWhenHasStock() {
        assertTrue(testProduct.isInStock());
    }

    @Test
    @DisplayName("Product isInStock should return false when stock = 0")
    void product_IsInStock_ReturnsFalseWhenNoStock() {
        testProduct.setStockQuantity(0);
        assertFalse(testProduct.isInStock());
    }

    @Test
    @DisplayName("Product isActive should return true when active and status = 1")
    void product_IsActive_ReturnsTrueWhenActiveAndStatus1() {
        assertTrue(testProduct.isActive());
    }

    @Test
    @DisplayName("Product isActive should return false when inactive")
    void product_IsActive_ReturnsFalseWhenInactive() {
        testProduct.setActive(false);
        assertFalse(testProduct.isActive());
    }
}
