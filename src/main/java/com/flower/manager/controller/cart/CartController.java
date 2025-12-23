package com.flower.manager.controller.cart;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.cart.AddToCartRequest;
import com.flower.manager.dto.cart.CartDTO;
import com.flower.manager.dto.cart.UpdateCartItemRequest;
import com.flower.manager.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các API liên quan đến Giỏ hàng
 * Endpoint: /api/cart/**
 * 
 * Yêu cầu: User phải đăng nhập
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    /**
     * Lấy thông tin giỏ hàng hiện tại
     * GET /api/cart
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart() {
        log.info("Getting cart for current user");
        CartDTO cart = cartService.getCart();
        return ResponseEntity.ok(ApiResponse.success(cart, "Lấy giỏ hàng thành công"));
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     * POST /api/cart/add
     * 
     * Body: { "productId": 1, "quantity": 2 }
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        log.info("Adding product {} to cart, quantity: {}", request.getProductId(), request.getQuantity());
        CartDTO cart = cartService.addItem(request);
        return ResponseEntity.ok(ApiResponse.success(cart, "Đã thêm sản phẩm vào giỏ hàng"));
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     * PUT /api/cart/update
     * 
     * Body: { "productId": 1, "quantity": 5 }
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartDTO>> updateQuantity(@Valid @RequestBody UpdateCartItemRequest request) {
        log.info("Updating quantity for product {}: {}", request.getProductId(), request.getQuantity());
        CartDTO cart = cartService.updateItem(request);
        return ResponseEntity.ok(ApiResponse.success(cart, "Đã cập nhật số lượng"));
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     * DELETE /api/cart/remove/{productId}
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeItem(@PathVariable Long productId) {
        log.info("Removing product {} from cart", productId);
        CartDTO cart = cartService.removeItem(productId);
        return ResponseEntity.ok(ApiResponse.success(cart, "Đã xóa sản phẩm khỏi giỏ hàng"));
    }

    /**
     * Xóa toàn bộ giỏ hàng
     * DELETE /api/cart/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart() {
        log.info("Clearing cart");
        cartService.clearCart();
        return ResponseEntity.ok(ApiResponse.success("Đã xóa toàn bộ giỏ hàng"));
    }
}
