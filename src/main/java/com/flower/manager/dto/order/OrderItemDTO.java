package com.flower.manager.dto.order;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO cho thông tin sản phẩm trong đơn hàng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String productThumbnail;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
