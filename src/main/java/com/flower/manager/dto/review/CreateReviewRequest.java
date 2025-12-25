package com.flower.manager.dto.review;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO để tạo review mới
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "Product ID là bắt buộc")
    private Long productId;

    @NotNull(message = "Order ID là bắt buộc")
    private Long orderId;

    @NotNull(message = "Rating là bắt buộc")
    @Min(value = 1, message = "Rating phải từ 1-5")
    @Max(value = 5, message = "Rating phải từ 1-5")
    private Integer rating;

    @Size(max = 2000, message = "Bình luận tối đa 2000 ký tự")
    private String comment;

    /**
     * Danh sách URLs ảnh (đã upload trước đó)
     * Tối đa 5 ảnh
     */
    @Size(max = 5, message = "Tối đa 5 ảnh")
    private List<String> images;
}
