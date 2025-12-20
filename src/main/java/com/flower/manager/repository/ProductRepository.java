package com.flower.manager.repository;

import com.flower.manager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        // ============ Lấy sản phẩm theo danh mục ============

        /**
         * Lấy sản phẩm theo ID danh mục, chỉ lấy active
         */
        List<Product> findByCategoryIdAndActiveTrueOrderByCreatedAtDesc(Long categoryId);

        /**
         * Lấy sản phẩm theo ID danh mục (bao gồm cả inactive)
         */
        List<Product> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

        // ============ Lấy sản phẩm theo status ============

        /**
         * Lấy sản phẩm theo status
         */
        List<Product> findByStatusOrderByCreatedAtDesc(Integer status);

        /**
         * Lấy tất cả sản phẩm active
         */
        List<Product> findByActiveTrueOrderByCreatedAtDesc();

        /**
         * Lấy tất cả sản phẩm (sắp xếp theo ngày tạo)
         */
        List<Product> findAllByOrderByCreatedAtDesc();

        // ============ Tìm kiếm ============

        /**
         * Tìm sản phẩm theo slug
         */
        Optional<Product> findBySlug(String slug);

        /**
         * Kiểm tra slug đã tồn tại (trừ ID đang update)
         */
        boolean existsBySlugAndIdNot(String slug, Long id);

        /**
         * Kiểm tra slug đã tồn tại
         */
        boolean existsBySlug(String slug);

        /**
         * Tìm kiếm sản phẩm theo tên (chứa keyword)
         */
        List<Product> findByNameContainingIgnoreCaseAndActiveTrueOrderByCreatedAtDesc(String name);

        /**
         * Tìm kiếm sản phẩm theo tên (bao gồm inactive)
         */
        List<Product> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);

        // ============ Query tùy chỉnh ============

        /**
         * Lấy sản phẩm với Eager fetch category
         */
        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN FETCH p.category " +
                        "WHERE p.active = true " +
                        "ORDER BY p.createdAt DESC")
        List<Product> findAllActiveWithCategory();

        /**
         * Lấy sản phẩm theo danh mục với Eager fetch
         */
        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN FETCH p.category " +
                        "WHERE p.category.id = :categoryId AND p.active = true " +
                        "ORDER BY p.createdAt DESC")
        List<Product> findByCategoryIdWithCategory(@Param("categoryId") Long categoryId);

        /**
         * Lấy sản phẩm đang giảm giá
         */
        @Query("SELECT p FROM Product p " +
                        "WHERE p.salePrice IS NOT NULL AND p.salePrice > 0 AND p.salePrice < p.price AND p.active = true "
                        +
                        "ORDER BY p.createdAt DESC")
        List<Product> findOnSaleProducts();

        /**
         * Lấy sản phẩm mới nhất (limit)
         */
        @Query("SELECT p FROM Product p " +
                        "WHERE p.active = true " +
                        "ORDER BY p.createdAt DESC " +
                        "LIMIT :limit")
        List<Product> findLatestProducts(@Param("limit") int limit);

        /**
         * Đếm số sản phẩm theo danh mục
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
        long countByCategoryId(@Param("categoryId") Long categoryId);

        /**
         * Kiểm tra danh mục có sản phẩm không
         */
        @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.category.id = :categoryId")
        boolean existsByCategoryId(@Param("categoryId") Long categoryId);

        // ============ Lấy sản phẩm theo danh mục cha ============

        /**
         * Lấy sản phẩm thuộc danh mục cha (bao gồm tất cả sản phẩm từ các danh mục con)
         * Sử dụng khi click vào danh mục cha để hiển thị tất cả sản phẩm
         */
        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN FETCH p.category c " +
                        "WHERE c.parent.id = :parentCategoryId AND p.active = true " +
                        "ORDER BY p.createdAt DESC")
        List<Product> findByParentCategoryIdAndActiveTrue(@Param("parentCategoryId") Long parentCategoryId);

        /**
         * Lấy sản phẩm với eager fetch category và parent category
         * Để tránh N+1 query khi lấy thông tin parent
         */
        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN FETCH p.category c " +
                        "LEFT JOIN FETCH c.parent " +
                        "WHERE p.category.id = :categoryId AND p.active = true " +
                        "ORDER BY p.createdAt DESC")
        List<Product> findByCategoryIdWithParentCategory(@Param("categoryId") Long categoryId);

        /**
         * Lấy sản phẩm thuộc danh mục cha theo slug của danh mục cha
         */
        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN FETCH p.category c " +
                        "LEFT JOIN FETCH c.parent parent " +
                        "WHERE parent.slug = :parentCategorySlug AND p.active = true " +
                        "ORDER BY p.createdAt DESC")
        List<Product> findByParentCategorySlugAndActiveTrue(@Param("parentCategorySlug") String parentCategorySlug);
}
