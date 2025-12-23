package com.flower.manager.dto.order;

import com.flower.manager.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO cho yêu cầu cập nhật trạng thái đơn hàng (Admin)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private OrderStatus status;

    private String reason; // Lý do (dùng khi hủy đơn)
}
