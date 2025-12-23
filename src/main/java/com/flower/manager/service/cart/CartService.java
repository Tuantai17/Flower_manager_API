package com.flower.manager.service.cart;

import com.flower.manager.dto.cart.AddToCartRequest;
import com.flower.manager.dto.cart.CartDTO;
import com.flower.manager.dto.cart.UpdateCartItemRequest;

/**
 * Service interface cho Cart
 */
public interface CartService {

    /**
     * Lấy giỏ hàng của user hiện tại
     */
    CartDTO getCart();

    /**
     * Thêm sản phẩm vào giỏ hàng
     * Nếu sản phẩm đã có, cộng dồn số lượng
     */
    CartDTO addItem(AddToCartRequest request);

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    CartDTO updateItem(UpdateCartItemRequest request);

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    CartDTO removeItem(Long productId);

    /**
     * Xóa toàn bộ giỏ hàng
     */
    void clearCart();
}
