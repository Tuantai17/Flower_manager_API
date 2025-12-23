package com.flower.manager.dto.cart;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO cho thông tin từng sản phẩm trong giỏ hàng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String productThumbnail;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
