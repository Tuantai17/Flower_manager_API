package com.flower.manager.repository;

import com.flower.manager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ============ Lấy danh mục cha (cấp 1) ============

    /**
     * Lấy tất cả danh mục cha (parent = null) đang active
     */
    List<Category> findByParentIsNullAndActiveTrueOrderBySortOrderAsc();

    /**
     * Lấy tất cả danh mục cha (parent = null), bao gồm cả inactive
     */
    List<Category> findByParentIsNullOrderBySortOrderAsc();

    // ============ Lấy danh mục con (cấp 2) ============

    /**
     * Lấy danh mục con theo ID cha, chỉ lấy active
     */
    List<Category> findByParentIdAndActiveTrueOrderBySortOrderAsc(Long parentId);

    /**
     * Lấy danh mục con theo ID cha, bao gồm cả inactive
     */
    List<Category> findByParentIdOrderBySortOrderAsc(Long parentId);

    // ============ Tìm kiếm ============

    /**
     * Tìm theo slug
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Kiểm tra slug đã tồn tại (trừ ID đang update)
     */
    boolean existsBySlugAndIdNot(String slug, Long id);

    /**
     * Kiểm tra slug đã tồn tại
     */
    boolean existsBySlug(String slug);

    /**
     * Tìm kiếm theo tên (chứa keyword)
     */
    List<Category> findByNameContainingIgnoreCaseOrderBySortOrderAsc(String name);

    /**
     * Lấy tất cả danh mục active
     */
    List<Category> findByActiveTrueOrderBySortOrderAsc();

    // ============ Query tùy chỉnh ============

    /**
     * Lấy danh mục cha với các con (Eager fetch để tránh N+1)
     */
    @Query("SELECT DISTINCT c FROM Category c " +
            "LEFT JOIN FETCH c.children " +
            "WHERE c.parent IS NULL AND c.active = true " +
            "ORDER BY c.sortOrder ASC")
    List<Category> findAllParentCategoriesWithChildren();

    /**
     * Đếm số danh mục con của một danh mục cha
     */
    @Query("SELECT COUNT(c) FROM Category c WHERE c.parent.id = :parentId")
    long countByParentId(@Param("parentId") Long parentId);

    /**
     * Kiểm tra danh mục có con không
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.parent.id = :parentId")
    boolean hasChildren(@Param("parentId") Long parentId);
}
