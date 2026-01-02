package com.flower.manager.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO cho việc tạo Product (Create)
 * Chỉ chứa các field cần thiết khi tạo mới
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200, message = "Tên sản phẩm tối đa 200 ký tự")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 200, message = "Slug tối đa 200 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ được chứa chữ thường, số và dấu gạch ngang")
    private String slug;

    @Size(max = 100, message = "Mã SKU tối đa 100 ký tự")
    private String sku;

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
     * ID của danh mục (bắt buộc)
     */
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
}
