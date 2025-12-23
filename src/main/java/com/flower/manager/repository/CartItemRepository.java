package com.flower.manager.repository;

import com.flower.manager.entity.Cart;
import com.flower.manager.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho CartItem entity
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Tìm tất cả items theo Cart
     */
    List<CartItem> findByCart(Cart cart);

    /**
     * Tìm tất cả items theo Cart ID
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * Tìm item theo Cart ID và Product ID
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Xóa tất cả items theo Cart ID
     */
    void deleteByCartId(Long cartId);

    /**
     * Kiểm tra sản phẩm đã có trong giỏ chưa
     */
    boolean existsByCartIdAndProductId(Long cartId, Long productId);
}
