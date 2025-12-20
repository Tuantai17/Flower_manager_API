package com.flower.manager.mapper;

import com.flower.manager.dto.ProductCreateDTO;
import com.flower.manager.dto.ProductDTO;
import com.flower.manager.dto.ProductUpdateDTO;
import com.flower.manager.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper chuyển đổi giữa Product Entity và ProductDTO
 * Tương tự như CategoryMapper
 */
@Component
public class ProductMapper {

    // ============ Entity -> DTO ============

    /**
     * Chuyển Product entity sang ProductDTO
     * Bao gồm thông tin danh mục cha nếu có
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        // Lấy thông tin danh mục cha nếu có
        Long parentCategoryId = null;
        String parentCategoryName = null;
        String parentCategorySlug = null;

        if (product.getCategory() != null && product.getCategory().getParent() != null) {
            parentCategoryId = product.getCategory().getParent().getId();
            parentCategoryName = product.getCategory().getParent().getName();
            parentCategorySlug = product.getCategory().getParent().getSlug();
        }

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .thumbnail(product.getThumbnail())
                .stockQuantity(product.getStockQuantity())
                .status(product.getStatus())
                .active(product.getActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categorySlug(product.getCategory() != null ? product.getCategory().getSlug() : null)
                .parentCategoryId(parentCategoryId)
                .parentCategoryName(parentCategoryName)
                .parentCategorySlug(parentCategorySlug)
                .onSale(product.isOnSale())
                .currentPrice(product.getCurrentPrice())
                .build();
    }

    /**
     * Chuyển danh sách Product entity sang danh sách ProductDTO
     */
    public List<ProductDTO> toDTOList(List<Product> products) {
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ============ DTO -> Entity ============

    /**
     * Chuyển ProductCreateDTO sang Product entity (dùng cho create)
     * Lưu ý: category sẽ được set trong service layer
     */
    public Product toEntity(ProductCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setSlug(dto.getSlug());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setSalePrice(dto.getSalePrice());
        product.setThumbnail(dto.getThumbnail());
        product.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        product.setActive(dto.getActive() != null ? dto.getActive() : true);
        // category sẽ được set trong service layer
        return product;
    }

    /**
     * Cập nhật Product entity từ ProductUpdateDTO (dùng cho update)
     */
    public void updateEntity(Product product, ProductUpdateDTO dto) {
        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getSlug() != null) {
            product.setSlug(dto.getSlug());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getSalePrice() != null) {
            product.setSalePrice(dto.getSalePrice());
        }
        if (dto.getThumbnail() != null) {
            product.setThumbnail(dto.getThumbnail());
        }
        if (dto.getStockQuantity() != null) {
            product.setStockQuantity(dto.getStockQuantity());
        }
        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }
        if (dto.getActive() != null) {
            product.setActive(dto.getActive());
        }
        // category sẽ được update trong service layer nếu categoryId thay đổi
    }
}
