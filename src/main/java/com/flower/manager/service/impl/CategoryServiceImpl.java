package com.flower.manager.service.impl;

import com.flower.manager.dto.CategoryDTO;
import com.flower.manager.dto.CategoryMenuDTO;
import com.flower.manager.entity.Category;
import com.flower.manager.mapper.CategoryMapper;
import com.flower.manager.repository.CategoryRepository;
import com.flower.manager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation của CategoryService
 * Hỗ trợ cấu trúc đa cấp 2 lớp (Cha - Con)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    // ============ CRUD Operations ============

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        log.info("Creating category: {}", dto.getName());

        // Kiểm tra slug đã tồn tại chưa
        if (repository.existsBySlug(dto.getSlug())) {
            throw new IllegalArgumentException("Slug đã tồn tại: " + dto.getSlug());
        }

        Category category = mapper.toEntity(dto);

        // Nếu có parentId -> đây là danh mục con (cấp 2)
        if (dto.getParentId() != null) {
            Category parent = repository.findById(dto.getParentId())
                    .orElseThrow(
                            () -> new RuntimeException("Không tìm thấy danh mục cha với ID: " + dto.getParentId()));

            // Kiểm tra parent không phải là danh mục con (chỉ cho phép 2 cấp)
            if (parent.getParent() != null) {
                throw new IllegalArgumentException(
                        "Chỉ hỗ trợ cấu trúc 2 cấp. Danh mục cha không được là danh mục con của danh mục khác.");
            }

            category.setParent(parent);
        }

        Category saved = repository.save(category);
        log.info("Created category with ID: {}", saved.getId());

        return mapper.toDTO(saved);
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO dto) {
        log.info("Updating category with ID: {}", id);

        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra slug nếu có thay đổi
        if (dto.getSlug() != null && !dto.getSlug().equals(category.getSlug())) {
            if (repository.existsBySlugAndIdNot(dto.getSlug(), id)) {
                throw new IllegalArgumentException("Slug đã tồn tại: " + dto.getSlug());
            }
        }

        // Cập nhật thông tin cơ bản
        mapper.updateEntity(category, dto);

        // Xử lý thay đổi parent
        if (dto.getParentId() != null) {
            // Không cho phép set parent là chính nó
            if (dto.getParentId().equals(id)) {
                throw new IllegalArgumentException("Danh mục không thể là cha của chính nó");
            }

            Category newParent = repository.findById(dto.getParentId())
                    .orElseThrow(
                            () -> new RuntimeException("Không tìm thấy danh mục cha với ID: " + dto.getParentId()));

            // Kiểm tra parent không phải là danh mục con (chỉ cho phép 2 cấp)
            if (newParent.getParent() != null) {
                throw new IllegalArgumentException(
                        "Chỉ hỗ trợ cấu trúc 2 cấp. Danh mục cha không được là danh mục con của danh mục khác.");
            }

            // Kiểm tra nếu category hiện tại có children thì không cho phép chuyển thành
            // con
            if (repository.hasChildren(id)) {
                throw new IllegalArgumentException(
                        "Không thể chuyển danh mục cha thành danh mục con khi vẫn còn danh mục con");
            }

            category.setParent(newParent);
        } else {
            // Nếu parentId = null -> chuyển thành danh mục cha (cấp 1)
            category.setParent(null);
        }

        Category saved = repository.save(category);
        log.info("Updated category with ID: {}", saved.getId());

        return mapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting category with ID: {}", id);

        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra có danh mục con không
        if (repository.hasChildren(id)) {
            throw new IllegalArgumentException(
                    "Không thể xóa danh mục khi vẫn còn danh mục con. Vui lòng xóa danh mục con trước.");
        }

        repository.delete(category);
        log.info("Deleted category with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getById(Long id) {
        log.info("Getting category by ID: {}", id);

        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        return mapper.toDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getBySlug(String slug) {
        log.info("Getting category by slug: {}", slug);

        Category category = repository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với slug: " + slug));

        return mapper.toDTO(category);
    }

    // ============ Danh sách ============

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAll() {
        log.info("Getting all categories");
        return mapper.toDTOList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllParentCategories() {
        log.info("Getting all parent categories");
        return mapper.toDTOList(repository.findByParentIsNullOrderBySortOrderAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getChildrenByParentId(Long parentId) {
        log.info("Getting children by parent ID: {}", parentId);
        return mapper.toDTOList(repository.findByParentIdOrderBySortOrderAsc(parentId));
    }

    // ============ Menu ============

    @Override
    @Transactional(readOnly = true)
    public List<CategoryMenuDTO> getMenu() {
        log.info("Getting menu categories");

        // Lấy tất cả danh mục cha (cấp 1) active
        List<Category> parentCategories = repository.findByParentIsNullAndActiveTrueOrderBySortOrderAsc();

        // Chuyển đổi và build menu đệ quy
        return parentCategories.stream()
                .map(this::buildMenu)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryMenuDTO getMenuByParentId(Long parentId) {
        log.info("Getting menu by parent ID: {}", parentId);

        Category category = repository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + parentId));

        return buildMenu(category);
    }

    /**
     * Build menu đệ quy (hỗ trợ đa cấp)
     */
    private CategoryMenuDTO buildMenu(Category category) {
        CategoryMenuDTO dto = CategoryMenuDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .build();

        // Lấy danh mục con active
        List<Category> children = repository.findByParentIdAndActiveTrueOrderBySortOrderAsc(category.getId());

        if (!children.isEmpty()) {
            dto.setChildren(
                    children.stream()
                            .map(this::buildMenu) // Đệ quy cho children
                            .toList());
        }

        return dto;
    }

    // ============ Utility ============

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug, Long excludeId) {
        if (excludeId != null) {
            return repository.existsBySlugAndIdNot(slug, excludeId);
        }
        return repository.existsBySlug(slug);
    }
}
