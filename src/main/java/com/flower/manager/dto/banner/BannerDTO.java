package com.flower.manager.dto.banner;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDTO {
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
    private String title;

    @NotBlank(message = "URL hình ảnh không được để trống")
    @Size(max = 500, message = "URL hình ảnh tối đa 500 ký tự")
    private String imageUrl;

    @Size(max = 500, message = "Link URL tối đa 500 ký tự")
    private String linkUrl;

    @Size(max = 200, message = "Phụ đề tối đa 200 ký tự")
    private String subtitle;

    @Size(max = 50, message = "Text nút tối đa 50 ký tự")
    private String buttonText;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Màu nền phải theo định dạng hex (#RRGGBB)")
    private String backgroundColor;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Màu chữ phải theo định dạng hex (#RRGGBB)")
    private String textColor;

    private Integer sortOrder;

    private Boolean active;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
