package com.flower.manager.service;

import com.flower.manager.dto.CategoryDTO;
import com.flower.manager.dto.CategoryMenuDTO;

import java.util.List;

/**
 * Service interface cho Category - Hỗ trợ đa cấp cha-con
 */
public interface CategoryService {

    // ============ CRUD Operations ============

    /**
     * Tạo danh mục mới
     * 
     * @param dto thông tin danh mục
     * @return danh mục đã tạo
     */
    CategoryDTO create(CategoryDTO dto);

    /**
     * Cập nhật danh mục
     * 
     * @param id  ID danh mục
     * @param dto thông tin cập nhật
     * @return danh mục đã cập nhật
     */
    CategoryDTO update(Long id, CategoryDTO dto);

    /**
     * Xóa danh mục
     * 
     * @param id ID danh mục
     */
    void delete(Long id);

    /**
     * Lấy danh mục theo ID
     * 
     * @param id ID danh mục
     * @return danh mục
     */
    CategoryDTO getById(Long id);

    /**
     * Lấy danh mục theo slug
     * 
     * @param slug slug danh mục
     * @return danh mục
     */
    CategoryDTO getBySlug(String slug);

    // ============ Danh sách ============

    /**
     * Lấy tất cả danh mục
     * 
     * @return danh sách tất cả danh mục
     */
    List<CategoryDTO> getAll();

    /**
     * Lấy tất cả danh mục cha (cấp 1)
     * 
     * @return danh sách danh mục cha
     */
    List<CategoryDTO> getAllParentCategories();

    /**
     * Lấy tất cả danh mục con theo ID cha
     * 
     * @param parentId ID danh mục cha
     * @return danh sách danh mục con
     */
    List<CategoryDTO> getChildrenByParentId(Long parentId);

    // ============ Menu ============

    /**
     * Lấy menu danh mục (đa cấp cha-con) cho public
     * 
     * @return danh sách menu danh mục
     */
    List<CategoryMenuDTO> getMenu();

    /**
     * Lấy menu danh mục theo ID cha
     * 
     * @param parentId ID danh mục cha
     * @return menu danh mục với children
     */
    CategoryMenuDTO getMenuByParentId(Long parentId);

    // ============ Utility ============

    /**
     * Kiểm tra danh mục có tồn tại không
     * 
     * @param id ID danh mục
     * @return true nếu tồn tại
     */
    boolean existsById(Long id);

    /**
     * Kiểm tra slug đã tồn tại chưa
     * 
     * @param slug      slug cần kiểm tra
     * @param excludeId ID cần loại trừ (dùng khi update)
     * @return true nếu đã tồn tại
     */
    boolean existsBySlug(String slug, Long excludeId);
}
