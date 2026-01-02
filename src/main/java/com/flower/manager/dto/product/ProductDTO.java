package com.flower.manager.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

/**
 * DTO cho việc tạo/cập nhật Product
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductDTO extends RepresentationModel<ProductDTO> {

    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200, message = "Tên sản phẩm tối đa 200 ký tự")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 200, message = "Slug tối đa 200 ký tự")
    private String slug;

    @Size(max = 5000, message = "Mô tả tối đa 5000 ký tự")
    private String description;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Giá khuyến mãi không được âm")
    private BigDecimal salePrice;

    @Size(max = 500, message = "URL hình ảnh tối đa 500 ký tự")
    private String thumbnail;

    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    @Builder.Default
    private Integer stockQuantity = 0;

    /**
     * Trạng thái sản phẩm:
     * 1 = Active (đang bán)
     * 0 = Inactive (ngừng bán)
     * 2 = Out of stock (hết hàng)
     */
    @Min(value = 0, message = "Status không hợp lệ")
    @Max(value = 2, message = "Status không hợp lệ")
    @Builder.Default
    private Integer status = 1;

    @Builder.Default
    private Boolean active = true;

    /**
     * ID của danh mục
     */
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    /**
     * Tên danh mục (read-only, không dùng khi create/update)
     */
    private String categoryName;

    /**
     * Slug danh mục (read-only)
     */
    private String categorySlug;

    /**
     * ID danh mục cha (read-only)
     * Nếu categoryId là danh mục con, đây là ID của danh mục cha
     * Nếu categoryId là danh mục cha, đây sẽ là null
     */
    private Long parentCategoryId;

    /**
     * Tên danh mục cha (read-only)
     */
    private String parentCategoryName;

    /**
     * Slug danh mục cha (read-only)
     */
    private String parentCategorySlug;

    /**
     * Sản phẩm có đang giảm giá không (read-only)
     */
    private Boolean onSale;

    /**
     * Giá hiện tại (giá sale nếu có) (read-only)
     */
    private BigDecimal currentPrice;

    /**
     * Ngày tạo sản phẩm (read-only)
     */
    private LocalDateTime createdAt;

    /**
     * Ngày cập nhật sản phẩm (read-only)
     */
    private LocalDateTime updatedAt;

    /**
     * Mã SKU sản phẩm (read-only)
     */
    private String sku;

    /**
     * Số lượng đã bán (read-only)
     */
    private Integer soldCount;
}
