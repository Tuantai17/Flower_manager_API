package com.flower.manager.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO cho Admin phản hồi review
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminReplyRequest {

    @NotBlank(message = "Nội dung phản hồi không được để trống")
    @Size(max = 1000, message = "Phản hồi tối đa 1000 ký tự")
    private String reply;

    /**
     * Danh sách URL ảnh đính kèm (optional)
     */
    private List<String> images;
}
