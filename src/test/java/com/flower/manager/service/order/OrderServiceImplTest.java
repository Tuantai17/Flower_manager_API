package com.flower.manager.service.order;

import com.flower.manager.entity.*;
import com.flower.manager.enums.OrderStatus;
import com.flower.manager.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests cho Order Entity
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Order Unit Tests")
class OrderServiceImplTest {

    private User testUser;
    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .phoneNumber("0912345678")
                .fullName("Test User")
                .role(Role.CUSTOMER)
                .isActive(true)
                .build();

        // Setup test order
        testOrder = Order.builder()
                .id(1L)
                .orderCode("ORD123456")
                .user(testUser)
                .senderName("Test Sender")
                .senderPhone("0912345678")
                .recipientName("Test Recipient")
                .recipientPhone("0987654321")
                .province("TP.HCM")
                .district("Quận 1")
                .addressDetail("123 Test Street")
                .totalPrice(new BigDecimal("250000"))
                .finalPrice(new BigDecimal("250000"))
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.COD)
                .isPaid(false)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        // Setup test order item
        testOrderItem = OrderItem.builder()
                .id(1L)
                .order(testOrder)
                .productName("Bó hoa hồng")
                .quantity(2)
                .price(new BigDecimal("125000"))
                .build();
        testOrder.getItems().add(testOrderItem);
    }

    @Nested
    @DisplayName("Order isCancellable Tests")
    class IsCancellableTests {

        @Test
        @DisplayName("Order should be cancellable when PENDING")
        void isCancellable_WhenPending_ReturnsTrue() {
            testOrder.setStatus(OrderStatus.PENDING);
            assertTrue(testOrder.isCancellable());
        }

        @Test
        @DisplayName("Order should be cancellable when CONFIRMED")
        void isCancellable_WhenConfirmed_ReturnsTrue() {
            testOrder.setStatus(OrderStatus.CONFIRMED);
            assertTrue(testOrder.isCancellable());
        }

        @Test
        @DisplayName("Order should not be cancellable when PROCESSING")
        void isCancellable_WhenProcessing_ReturnsFalse() {
            testOrder.setStatus(OrderStatus.PROCESSING);
            assertFalse(testOrder.isCancellable());
        }

        @Test
        @DisplayName("Order should not be cancellable when SHIPPING")
        void isCancellable_WhenShipping_ReturnsFalse() {
            testOrder.setStatus(OrderStatus.SHIPPING);
            assertFalse(testOrder.isCancellable());
        }

        @Test
        @DisplayName("Order should not be cancellable when DELIVERED")
        void isCancellable_WhenDelivered_ReturnsFalse() {
            testOrder.setStatus(OrderStatus.DELIVERED);
            assertFalse(testOrder.isCancellable());
        }

        @Test
        @DisplayName("Order should not be cancellable when COMPLETED")
        void isCancellable_WhenCompleted_ReturnsFalse() {
            testOrder.setStatus(OrderStatus.COMPLETED);
            assertFalse(testOrder.isCancellable());
        }

        @Test
        @DisplayName("Order should not be cancellable when CANCELLED")
        void isCancellable_WhenCancelled_ReturnsFalse() {
            testOrder.setStatus(OrderStatus.CANCELLED);
            assertFalse(testOrder.isCancellable());
        }
    }

    @Nested
    @DisplayName("Order getTotalQuantity Tests")
    class GetTotalQuantityTests {

        @Test
        @DisplayName("Should return correct total quantity")
        void getTotalQuantity_ReturnsCorrectSum() {
            // testOrderItem has quantity = 2
            assertEquals(2, testOrder.getTotalQuantity());
        }

        @Test
        @DisplayName("Should return 0 for empty order")
        void getTotalQuantity_EmptyOrder_ReturnsZero() {
            testOrder.setItems(new ArrayList<>());
            assertEquals(0, testOrder.getTotalQuantity());
        }

        @Test
        @DisplayName("Should sum multiple items correctly")
        void getTotalQuantity_MultipleItems_CorrectSum() {
            OrderItem secondItem = OrderItem.builder()
                    .id(2L)
                    .order(testOrder)
                    .productName("Another Product")
                    .quantity(3)
                    .price(new BigDecimal("100000"))
                    .build();
            testOrder.getItems().add(secondItem);

            // 2 + 3 = 5
            assertEquals(5, testOrder.getTotalQuantity());
        }
    }

    @Nested
    @DisplayName("OrderStatus Display Name Tests")
    class OrderStatusTests {

        @Test
        @DisplayName("PENDING should have correct display name")
        void pendingStatus_HasCorrectDisplayName() {
            assertEquals("Chờ xử lý", OrderStatus.PENDING.getDisplayName());
        }

        @Test
        @DisplayName("CONFIRMED should have correct display name")
        void confirmedStatus_HasCorrectDisplayName() {
            assertEquals("Đã xác nhận", OrderStatus.CONFIRMED.getDisplayName());
        }

        @Test
        @DisplayName("DELIVERED should have correct display name")
        void deliveredStatus_HasCorrectDisplayName() {
            assertEquals("Đã giao hàng", OrderStatus.DELIVERED.getDisplayName());
        }

        @Test
        @DisplayName("CANCELLED should have correct display name")
        void cancelledStatus_HasCorrectDisplayName() {
            assertEquals("Đã hủy", OrderStatus.CANCELLED.getDisplayName());
        }
    }

    @Nested
    @DisplayName("PaymentMethod Display Name Tests")
    class PaymentMethodTests {

        @Test
        @DisplayName("COD should have correct display name")
        void cod_HasCorrectDisplayName() {
            assertEquals("Thanh toán khi nhận hàng", PaymentMethod.COD.getDisplayName());
        }

        @Test
        @DisplayName("MOMO should have correct display name")
        void momo_HasCorrectDisplayName() {
            assertEquals("Ví MoMo", PaymentMethod.MOMO.getDisplayName());
        }
    }
}
