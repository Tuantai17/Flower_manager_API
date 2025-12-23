package com.flower.manager.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO tra ve sau khi upload file thanh cong
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    /**
     * URL cua file da upload
     */
    private String url;

    /**
     * Ten file goc
     */
    private String originalName;

    /**
     * Kich thuoc file (bytes)
     */
    private Long size;

    /**
     * Loai file (image/jpeg, image/png, ...)
     */
    private String contentType;
}
