package com.flower.manager.repository;

import com.flower.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Tìm user theo username
     */
    Optional<User> findByUsername(String username);

    /**
     * Tìm user theo email
     */
    Optional<User> findByEmail(String email);

    /**
     * Tìm user theo số điện thoại
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Tìm user theo username, email hoặc số điện thoại
     * Dùng cho chức năng đăng nhập
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier OR u.phoneNumber = :identifier")
    Optional<User> findByUsernameOrEmailOrPhoneNumber(@Param("identifier") String identifier);

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    Boolean existsByUsername(String username);

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    Boolean existsByEmail(String email);

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa
     */
    Boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Kiểm tra username đã tồn tại (ngoại trừ user hiện tại)
     */
    Boolean existsByUsernameAndIdNot(String username, Long id);

    /**
     * Kiểm tra email đã tồn tại (ngoại trừ user hiện tại)
     */
    Boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Kiểm tra số điện thoại đã tồn tại (ngoại trừ user hiện tại)
     */
    Boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    /**
     * Tìm kiếm users với các filter cho Admin
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "u.phoneNumber LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:isActive IS NULL OR u.isActive = :isActive)")
    org.springframework.data.domain.Page<User> findWithFilters(
            @Param("keyword") String keyword,
            @Param("role") com.flower.manager.entity.Role role,
            @Param("isActive") Boolean isActive,
            org.springframework.data.domain.Pageable pageable);

    /**
     * Đếm số users theo role
     */
    long countByRole(com.flower.manager.entity.Role role);
}
