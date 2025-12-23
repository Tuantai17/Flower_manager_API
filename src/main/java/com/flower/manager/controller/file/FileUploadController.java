package com.flower.manager.controller.file;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.file.FileUploadResponse;
import com.flower.manager.service.file.ImageStorageFactory;
import com.flower.manager.service.file.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Controller xu ly upload file anh
 * 
 * ENDPOINTS:
 * ==========
 * POST /api/upload/product - Upload anh san pham (ADMIN)
 * POST /api/upload/products - Upload nhieu anh san pham (ADMIN)
 * POST /api/upload/category - Upload anh danh muc (ADMIN)
 * POST /api/upload/user - Upload avatar user (Authenticated)
 * DELETE /api/upload?url=... - Xoa file (ADMIN)
 * GET /api/upload/info - Xem thong tin storage dang dung
 * 
 * CACH CHUYEN DOI STORAGE:
 * ========================
 * Trong application.properties:
 * - file.storage-mode=local -> Luu tren server
 * - file.storage-mode=cloudinary -> Luu tren Cloudinary cloud
 * - file.storage-mode=dual -> Luu song song ca 2 (Local + Cloudinary)
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final ImageStorageFactory storageFactory;

    /**
     * Lay storage service dua tren cau hinh
     */
    private ImageStorageService getStorage() {
        return storageFactory.getService();
    }

    /**
     * Xem thong tin storage dang su dung
     * GET /api/upload/info
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStorageInfo() {
        String mode = "local";
        String desc = "Dang su dung Local Storage (uploads/)";

        if (storageFactory.isCloudinaryMode()) {
            mode = "cloudinary";
            desc = "Dang su dung Cloudinary Cloud Storage";
        } else if (storageFactory.isDualMode()) {
            mode = "dual";
            desc = "Dang su dung Dual Storage (Local + Cloudinary)";
        }

        Map<String, Object> info = Map.of(
                "storageMode", mode,
                "description", desc);
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    /**
     * Upload anh san pham (chi Admin)
     * POST /api/upload/product
     */
    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadProductImage(
            @RequestParam("file") MultipartFile file) {

        String mode = storageFactory.isDualMode() ? "dual"
                : (storageFactory.isCloudinaryMode() ? "cloudinary" : "local");

        log.info("Uploading product image: {} (mode: {})",
                file.getOriginalFilename(), mode);

        String url = getStorage().uploadFile(file, "products");

        FileUploadResponse response = FileUploadResponse.builder()
                .url(url)
                .originalName(file.getOriginalFilename())
                .size(file.getSize())
                .contentType(file.getContentType())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Upload anh san pham thanh cong"));
    }

    /**
     * Upload nhieu anh san pham cung luc
     * POST /api/upload/products
     */
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<String>>> uploadProductImages(
            @RequestParam("files") MultipartFile[] files) {

        log.info("Uploading {} product images", files.length);

        List<String> urls = getStorage().uploadFiles(files, "products");

        return ResponseEntity.ok(ApiResponse.success(urls, "Upload " + urls.size() + " anh thanh cong"));
    }

    /**
     * Upload anh danh muc (chi Admin)
     * POST /api/upload/category
     */
    @PostMapping(value = "/category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadCategoryImage(
            @RequestParam("file") MultipartFile file) {

        log.info("Uploading category image: {}", file.getOriginalFilename());

        String url = getStorage().uploadFile(file, "categories");

        FileUploadResponse response = FileUploadResponse.builder()
                .url(url)
                .originalName(file.getOriginalFilename())
                .size(file.getSize())
                .contentType(file.getContentType())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Upload anh danh muc thanh cong"));
    }

    /**
     * Upload avatar user (User da dang nhap)
     * POST /api/upload/user
     */
    @PostMapping(value = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadUserAvatar(
            @RequestParam("file") MultipartFile file) {

        log.info("Uploading user avatar: {}", file.getOriginalFilename());

        String url = getStorage().uploadFile(file, "users");

        FileUploadResponse response = FileUploadResponse.builder()
                .url(url)
                .originalName(file.getOriginalFilename())
                .size(file.getSize())
                .contentType(file.getContentType())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Upload avatar thanh cong"));
    }

    /**
     * Xoa file (chi Admin)
     * DELETE /api/upload?url=...
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestParam("url") String fileUrl) {
        log.info("Deleting file: {}", fileUrl);

        boolean deleted = getStorage().deleteFile(fileUrl);

        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, "Xoa file thanh cong"));
        } else {
            return ResponseEntity.ok(ApiResponse.error(404, "File khong ton tai hoac da bi xoa"));
        }
    }
}
