package com.flower.manager.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO để cập nhật review (chỉ user tự cập nhật review của mình)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewRequest {

    @Min(value = 1, message = "Rating phải từ 1-5")
    @Max(value = 5, message = "Rating phải từ 1-5")
    private Integer rating;

    @Size(max = 2000, message = "Bình luận tối đa 2000 ký tự")
    private String comment;

    @Size(max = 5, message = "Tối đa 5 ảnh")
    private List<String> images;
}
