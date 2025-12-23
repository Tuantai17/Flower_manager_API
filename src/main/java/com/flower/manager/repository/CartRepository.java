package com.flower.manager.repository;

import com.flower.manager.entity.Cart;
import com.flower.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho Cart entity
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Tìm giỏ hàng theo User
     */
    Optional<Cart> findByUser(User user);

    /**
     * Tìm giỏ hàng theo User ID
     */
    Optional<Cart> findByUserId(Long userId);

    /**
     * Kiểm tra User đã có giỏ hàng chưa
     */
    boolean existsByUserId(Long userId);
}
