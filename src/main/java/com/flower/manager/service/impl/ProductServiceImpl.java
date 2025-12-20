package com.flower.manager.service.impl;

import com.flower.manager.dto.ProductCreateDTO;
import com.flower.manager.dto.ProductDTO;
import com.flower.manager.dto.ProductUpdateDTO;
import com.flower.manager.entity.Category;
import com.flower.manager.entity.Product;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.mapper.ProductMapper;
import com.flower.manager.repository.CategoryRepository;
import com.flower.manager.repository.ProductRepository;
import com.flower.manager.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Implementation của ProductService
 * Tương tự như CategoryServiceImpl
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    // ============ CRUD Operations ============

    @Override
    public ProductDTO create(ProductCreateDTO dto) {
        Objects.requireNonNull(dto, "ProductCreateDTO must not be null");
        log.info("Creating product: {}", dto.getName());

        // Kiểm tra slug đã tồn tại chưa
        if (productRepository.existsBySlug(dto.getSlug())) {
            throw new IllegalArgumentException("Slug đã tồn tại: " + dto.getSlug());
        }

        // Kiểm tra category tồn tại
        Long categoryId = Objects.requireNonNull(dto.getCategoryId(), "Category ID must not be null");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Product product = productMapper.toEntity(dto);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        log.info("Created product with ID: {}", saved.getId());

        return productMapper.toDTO(saved);
    }

    @Override
    public ProductDTO update(Long id, ProductUpdateDTO dto) {
        Objects.requireNonNull(id, "Product ID must not be null");
        Objects.requireNonNull(dto, "ProductUpdateDTO must not be null");
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Kiểm tra slug nếu có thay đổi
        if (dto.getSlug() != null && !dto.getSlug().equals(product.getSlug())) {
            if (productRepository.existsBySlugAndIdNot(dto.getSlug(), id)) {
                throw new IllegalArgumentException("Slug đã tồn tại: " + dto.getSlug());
            }
        }

        // Cập nhật thông tin cơ bản
        productMapper.updateEntity(product, dto);

        // Xử lý thay đổi category
        Long newCategoryId = dto.getCategoryId();
        Category currentCategory = product.getCategory();
        Long currentCategoryId = currentCategory != null ? currentCategory.getId() : null;

        if (newCategoryId != null && !newCategoryId.equals(currentCategoryId)) {
            Category newCategory = categoryRepository.findById(newCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", newCategoryId));
            product.setCategory(newCategory);
        }

        Product saved = productRepository.save(product);
        log.info("Updated product with ID: {}", saved.getId());

        return productMapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        Objects.requireNonNull(id, "Product ID must not be null");
        log.info("Deleting product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);
        log.info("Deleted product with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getById(Long id) {
        Objects.requireNonNull(id, "Product ID must not be null");
        log.info("Getting product by ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getBySlug(String slug) {
        log.info("Getting product by slug: {}", slug);

        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));

        return productMapper.toDTO(product);
    }

    // ============ Danh sách ============

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAll() {
        log.info("Getting all products");
        return productMapper.toDTOList(productRepository.findAllByOrderByCreatedAtDesc());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllActive() {
        log.info("Getting all active products");
        return productMapper.toDTOList(productRepository.findByActiveTrueOrderByCreatedAtDesc());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getByCategory(Long categoryId) {
        Objects.requireNonNull(categoryId, "Category ID must not be null");
        log.info("Getting products by category ID: {}", categoryId);

        // Kiểm tra category tồn tại
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }

        return productMapper.toDTOList(productRepository.findByCategoryIdAndActiveTrueOrderByCreatedAtDesc(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getByCategorySlug(String categorySlug) {
        log.info("Getting products by category slug: {}", categorySlug);

        Category category = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", categorySlug));

        Long categoryId = Objects.requireNonNull(category.getId(), "Category ID must not be null");
        return productMapper
                .toDTOList(productRepository.findByCategoryIdAndActiveTrueOrderByCreatedAtDesc(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchByName(String keyword) {
        log.info("Searching products by name: {}", keyword);
        return productMapper
                .toDTOList(productRepository.findByNameContainingIgnoreCaseAndActiveTrueOrderByCreatedAtDesc(keyword));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getOnSaleProducts() {
        log.info("Getting on-sale products");
        return productMapper.toDTOList(productRepository.findOnSaleProducts());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getLatestProducts(int limit) {
        log.info("Getting latest {} products", limit);
        return productMapper.toDTOList(productRepository.findLatestProducts(limit));
    }

    // ============ Utility ============

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        Objects.requireNonNull(id, "Product ID must not be null");
        return productRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug, Long excludeId) {
        if (excludeId != null) {
            return productRepository.existsBySlugAndIdNot(slug, excludeId);
        }
        return productRepository.existsBySlug(slug);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCategory(Long categoryId) {
        Objects.requireNonNull(categoryId, "Category ID must not be null");
        return productRepository.countByCategoryId(categoryId);
    }

    // ============ Lấy sản phẩm theo danh mục cha ============

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getByParentCategory(Long parentCategoryId) {
        Objects.requireNonNull(parentCategoryId, "Parent Category ID must not be null");
        log.info("Getting products by parent category ID: {}", parentCategoryId);

        // Kiểm tra category cha tồn tại
        Category parentCategory = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", parentCategoryId));

        // Kiểm tra đây có phải danh mục cha không (parent = null)
        if (parentCategory.getParent() != null) {
            log.warn("Category {} is not a parent category, returning products of this category only",
                    parentCategoryId);
            return productMapper.toDTOList(productRepository.findByCategoryIdWithParentCategory(parentCategoryId));
        }

        // Lấy tất cả sản phẩm từ các danh mục con
        return productMapper.toDTOList(productRepository.findByParentCategoryIdAndActiveTrue(parentCategoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getByParentCategorySlug(String parentCategorySlug) {
        log.info("Getting products by parent category slug: {}", parentCategorySlug);

        Category category = categoryRepository.findBySlug(parentCategorySlug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", parentCategorySlug));

        Long categoryId = Objects.requireNonNull(category.getId(), "Category ID must not be null");

        // Nếu là danh mục con, chỉ lấy sản phẩm của danh mục đó
        if (category.getParent() != null) {
            log.info("Category {} is a child category, returning its products only", parentCategorySlug);
            return productMapper.toDTOList(productRepository.findByCategoryIdWithParentCategory(categoryId));
        }

        // Nếu là danh mục cha, lấy tất cả sản phẩm từ các danh mục con
        return productMapper.toDTOList(productRepository.findByParentCategorySlugAndActiveTrue(parentCategorySlug));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getByCategoryIncludingChildren(Long categoryId) {
        Objects.requireNonNull(categoryId, "Category ID must not be null");
        log.info("Getting products by category (including children) ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        // Nếu là danh mục cha (parent = null) -> lấy tất cả sản phẩm từ các danh mục
        // con
        if (category.getParent() == null) {
            log.info("Category {} is a parent category, getting products from all child categories", categoryId);
            return productMapper.toDTOList(productRepository.findByParentCategoryIdAndActiveTrue(categoryId));
        }

        // Nếu là danh mục con -> chỉ lấy sản phẩm của danh mục đó
        log.info("Category {} is a child category, getting products of this category only", categoryId);
        return productMapper.toDTOList(productRepository.findByCategoryIdWithParentCategory(categoryId));
    }
}
