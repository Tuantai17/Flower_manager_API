package com.flower.manager.service.voucher;

import com.flower.manager.entity.Voucher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests cho Voucher Entity
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Voucher Unit Tests")
class VoucherServiceImplTest {

    private Voucher testVoucher;
    private Voucher percentVoucher;

    @BeforeEach
    void setUp() {
        // Voucher giảm số tiền cố định
        testVoucher = Voucher.builder()
                .id(1L)
                .code("SALE50K")
                .description("Giảm 50,000đ")
                .isPercent(false)
                .discountValue(new BigDecimal("50000"))
                .minOrderValue(new BigDecimal("200000"))
                .usageLimit(100)
                .usageCount(0)
                .isActive(true)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();

        // Voucher giảm theo phần trăm
        percentVoucher = Voucher.builder()
                .id(2L)
                .code("SALE10P")
                .description("Giảm 10%")
                .isPercent(true)
                .discountValue(new BigDecimal("10"))
                .maxDiscount(new BigDecimal("100000"))
                .minOrderValue(new BigDecimal("100000"))
                .usageLimit(50)
                .usageCount(10)
                .isActive(true)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Voucher Validation Tests")
    class VoucherValidationTests {

        @Test
        @DisplayName("Voucher should be valid when all conditions met")
        void isValid_AllConditionsMet_ReturnsTrue() {
            assertTrue(testVoucher.isValid());
        }

        @Test
        @DisplayName("Voucher should be invalid when expired")
        void isValid_WhenExpired_ReturnsFalse() {
            testVoucher.setEndDate(LocalDateTime.now().minusDays(1));
            assertFalse(testVoucher.isValid());
        }

        @Test
        @DisplayName("Voucher should be invalid when not started yet")
        void isValid_WhenNotStarted_ReturnsFalse() {
            testVoucher.setStartDate(LocalDateTime.now().plusDays(1));
            assertFalse(testVoucher.isValid());
        }

        @Test
        @DisplayName("Voucher should be invalid when usage limit reached")
        void isValid_WhenUsageLimitReached_ReturnsFalse() {
            testVoucher.setUsageCount(100);
            assertFalse(testVoucher.isValid());
        }

        @Test
        @DisplayName("Voucher should be invalid when inactive")
        void isValid_WhenInactive_ReturnsFalse() {
            testVoucher.setIsActive(false);
            assertFalse(testVoucher.isValid());
        }
    }

    @Nested
    @DisplayName("Discount Calculation Tests")
    class DiscountCalculationTests {

        @Test
        @DisplayName("Fixed discount should return exact discount value")
        void calculateDiscount_FixedType_ReturnsExactValue() {
            BigDecimal orderTotal = new BigDecimal("300000");
            BigDecimal discount = testVoucher.calculateDiscount(orderTotal);

            assertEquals(new BigDecimal("50000"), discount);
        }

        @Test
        @DisplayName("Percent discount should calculate correctly")
        void calculateDiscount_PercentType_CalculatesCorrectly() {
            BigDecimal orderTotal = new BigDecimal("500000");
            BigDecimal discount = percentVoucher.calculateDiscount(orderTotal);

            // 10% of 500000 = 50000
            assertEquals(new BigDecimal("50000"), discount);
        }

        @Test
        @DisplayName("Percent discount should respect max discount limit")
        void calculateDiscount_PercentType_RespectsMaxLimit() {
            BigDecimal orderTotal = new BigDecimal("2000000");
            BigDecimal discount = percentVoucher.calculateDiscount(orderTotal);

            // 10% of 2000000 = 200000, but max is 100000
            assertEquals(new BigDecimal("100000"), discount);
        }

        @Test
        @DisplayName("Should return zero when below min order value")
        void calculateDiscount_BelowMinOrder_ReturnsZero() {
            BigDecimal orderTotal = new BigDecimal("100000"); // Below 200000 min
            BigDecimal discount = testVoucher.calculateDiscount(orderTotal);

            assertEquals(BigDecimal.ZERO, discount);
        }

        @Test
        @DisplayName("Discount should not exceed order total")
        void calculateDiscount_DiscountExceedsTotal_ReturnsOrderTotal() {
            testVoucher.setDiscountValue(new BigDecimal("500000")); // Very high discount
            testVoucher.setMinOrderValue(BigDecimal.ZERO);

            BigDecimal orderTotal = new BigDecimal("200000");
            BigDecimal discount = testVoucher.calculateDiscount(orderTotal);

            assertEquals(orderTotal, discount);
        }
    }

    @Nested
    @DisplayName("Voucher Usage Tests")
    class VoucherUsageTests {

        @Test
        @DisplayName("Use should increment usage count")
        void use_IncrementsUsageCount() {
            int initialCount = testVoucher.getUsageCount();
            testVoucher.use();

            assertEquals(initialCount + 1, testVoucher.getUsageCount());
        }
    }
}
