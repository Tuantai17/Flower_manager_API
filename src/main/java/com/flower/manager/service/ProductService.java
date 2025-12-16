package com.flower.manager.service;

import com.flower.manager.dto.ProductDTO;

import java.util.List;

/**
 * Service interface cho Product
 * Tương tự như CategoryService
 */
public interface ProductService {

    // ============ CRUD Operations ============

    /**
     * Tạo sản phẩm mới
     *
     * @param dto thông tin sản phẩm
     * @return sản phẩm đã tạo
     */
    ProductDTO create(ProductDTO dto);

    /**
     * Cập nhật sản phẩm
     *
     * @param id  ID sản phẩm
     * @param dto thông tin cập nhật
     * @return sản phẩm đã cập nhật
     */
    ProductDTO update(Long id, ProductDTO dto);

    /**
     * Xóa sản phẩm
     *
     * @param id ID sản phẩm
     */
    void delete(Long id);

    /**
     * Lấy sản phẩm theo ID
     *
     * @param id ID sản phẩm
     * @return sản phẩm
     */
    ProductDTO getById(Long id);

    /**
     * Lấy sản phẩm theo slug
     *
     * @param slug slug sản phẩm
     * @return sản phẩm
     */
    ProductDTO getBySlug(String slug);

    // ============ Danh sách ============

    /**
     * Lấy tất cả sản phẩm
     *
     * @return danh sách tất cả sản phẩm
     */
    List<ProductDTO> getAll();

    /**
     * Lấy tất cả sản phẩm active
     *
     * @return danh sách sản phẩm active
     */
    List<ProductDTO> getAllActive();

    /**
     * Lấy sản phẩm theo ID danh mục
     *
     * @param categoryId ID danh mục
     * @return danh sách sản phẩm thuộc danh mục
     */
    List<ProductDTO> getByCategory(Long categoryId);

    /**
     * Lấy sản phẩm theo slug danh mục
     *
     * @param categorySlug slug danh mục
     * @return danh sách sản phẩm thuộc danh mục
     */
    List<ProductDTO> getByCategorySlug(String categorySlug);

    /**
     * Tìm kiếm sản phẩm theo tên
     *
     * @param keyword từ khóa tìm kiếm
     * @return danh sách sản phẩm
     */
    List<ProductDTO> searchByName(String keyword);

    /**
     * Lấy sản phẩm đang giảm giá
     *
     * @return danh sách sản phẩm đang giảm giá
     */
    List<ProductDTO> getOnSaleProducts();

    /**
     * Lấy sản phẩm mới nhất
     *
     * @param limit số lượng sản phẩm
     * @return danh sách sản phẩm mới nhất
     */
    List<ProductDTO> getLatestProducts(int limit);

    // ============ Utility ============

    /**
     * Kiểm tra sản phẩm có tồn tại không
     *
     * @param id ID sản phẩm
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

    /**
     * Đếm số sản phẩm theo danh mục
     *
     * @param categoryId ID danh mục
     * @return số lượng sản phẩm
     */
    long countByCategory(Long categoryId);
}
