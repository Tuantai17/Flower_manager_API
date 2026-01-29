package com.flower.manager.service.product;

import com.flower.manager.dto.product.ProductCreateDTO;
import com.flower.manager.dto.product.ProductDTO;
import com.flower.manager.dto.product.ProductSearchRequest;
import com.flower.manager.dto.product.ProductUpdateDTO;
import com.flower.manager.entity.Category;
import com.flower.manager.entity.Product;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.mapper.ProductMapper;
import com.flower.manager.repository.CategoryRepository;
import com.flower.manager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Implementation của ProductService
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

    // ============ Advanced Search ============

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> advancedSearch(ProductSearchRequest request) {
        log.info("Advanced search with filters: keyword={}, priceFrom={}, priceTo={}, categoryId={}, sortBy={}",
                request.getKeyword(), request.getPriceFrom(), request.getPriceTo(),
                request.getCategoryId(), request.getSortBy());

        String keyword = (request.getKeyword() != null && !request.getKeyword().isBlank())
                ? request.getKeyword().trim()
                : null;

        String sortBy = request.getSortBy() != null ? request.getSortBy().toLowerCase() : "newest";

        List<Product> products;

        switch (sortBy) {
            case "price_asc":
                products = productRepository.advancedSearchOrderByPriceAsc(
                        keyword, request.getPriceFrom(), request.getPriceTo(), request.getCategoryId());
                break;
            case "price_desc":
                products = productRepository.advancedSearchOrderByPriceDesc(
                        keyword, request.getPriceFrom(), request.getPriceTo(), request.getCategoryId());
                break;
            case "best_selling":
                // Lấy sản phẩm theo filter trước, sau đó sắp xếp theo best selling
                products = productRepository.advancedSearchOrderByNewest(
                        keyword, request.getPriceFrom(), request.getPriceTo(), request.getCategoryId());
                // Sắp xếp lại theo best selling nếu có dữ liệu
                List<Long> bestSellingIds = productRepository.findBestSellingProductIds();
                if (!bestSellingIds.isEmpty()) {
                    products = sortByBestSelling(products, bestSellingIds);
                }
                break;
            case "newest":
            default:
                products = productRepository.advancedSearchOrderByNewest(
                        keyword, request.getPriceFrom(), request.getPriceTo(), request.getCategoryId());
                break;
        }

        log.info("Found {} products matching search criteria", products.size());
        return productMapper.toDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getBestSellingProducts(int limit) {
        log.info("Getting top {} best selling products", limit);

        // Lấy danh sách product ID bán chạy
        List<Long> bestSellingIds = productRepository.findBestSellingProductIds();

        if (bestSellingIds.isEmpty()) {
            log.info("No best selling data found, returning latest products instead");
            return productMapper.toDTOList(productRepository.findLatestProducts(limit));
        }

        // Giới hạn số lượng ID
        List<Long> limitedIds = bestSellingIds.size() > limit
                ? bestSellingIds.subList(0, limit)
                : bestSellingIds;

        // Lấy products theo danh sách ID
        List<Product> products = productRepository.findByIdIn(limitedIds);

        // Sắp xếp lại theo thứ tự best selling
        products = sortByBestSelling(products, limitedIds);

        log.info("Returning {} best selling products", products.size());
        return productMapper.toDTOList(products);
    }

    /**
     * Sắp xếp danh sách Product theo thứ tự trong bestSellingIds
     */
    private List<Product> sortByBestSelling(List<Product> products, List<Long> bestSellingIds) {
        return products.stream()
                .sorted((p1, p2) -> {
                    int idx1 = bestSellingIds.indexOf(p1.getId());
                    int idx2 = bestSellingIds.indexOf(p2.getId());
                    // Sản phẩm không có trong best selling list sẽ xếp cuối
                    if (idx1 == -1)
                        idx1 = Integer.MAX_VALUE;
                    if (idx2 == -1)
                        idx2 = Integer.MAX_VALUE;
                    return Integer.compare(idx1, idx2);
                })
                .toList();
    }

    /**
     * Đồng bộ số lượng đã bán từ OrderItem vào Product
     * Chạy mỗi khi khởi động ứng dụng để đảm bảo dữ liệu đúng
     */
    @jakarta.annotation.PostConstruct
    @Transactional
    public void syncProductSoldCounts() {
        log.info("Starting synchronization of product sold counts...");
        try {
            // Lấy thống kê số lượng bán từ repository
            List<Object[]> soldStats = productRepository.findAllSoldProductStats();

            int updatedCount = 0;
            for (Object[] stat : soldStats) {
                Long productId = (Long) stat[0];
                Number totalSold = (Number) stat[1]; // SUM returns Long or BigDecimal

                if (productId != null && totalSold != null) {
                    Product product = productRepository.findById(productId).orElse(null);
                    if (product != null) {
                        product.setSoldCount(totalSold.intValue());
                        productRepository.save(product);
                        updatedCount++;
                    }
                }
            }
            log.info("Successfully synced sold counts for {} products", updatedCount);
        } catch (Exception e) {
            log.error("Failed to sync product sold counts: {}", e.getMessage());
        }
    }
}
