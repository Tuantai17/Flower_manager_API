package com.flower.manager.dto.cart;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO cho thông tin giỏ hàng đầy đủ
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {

    private Long id;
    private Long userId;
    private List<CartItemDTO> items;
    private Integer totalItems;
    private BigDecimal totalPrice;
}
