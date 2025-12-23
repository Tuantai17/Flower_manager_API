package com.flower.manager.service.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.flower.manager.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service upload anh len Cloudinary (Cloud Storage)
 * 
 * CACH HOAT DONG:
 * ===============
 * 1. User gui file anh qua API
 * 2. CloudinaryService upload file len Cloudinary server
 * 3. Cloudinary xu ly file:
 * - Luu file tren cloud
 * - Tu dong tao CDN URL (toc do truy cap nhanh toan cau)
 * - Tu dong optimize anh (nen, resize neu can)
 * 4. Tra ve URL cong khai (VD:
 * https://res.cloudinary.com/xxx/image/upload/v1234/folder/filename.jpg)
 * 5. Luu URL vao database (truong thumbnail cua Product)
 * 
 * UU DIEM SO VOI LOCAL STORAGE:
 * =============================
 * - Khong ton dung luong server
 * - CDN toan cau -> load anh nhanh hon
 * - Tu dong backup, khong mat du lieu
 * - Ho tro transform anh (resize, crop, filter) bang URL
 * - Mien phi 25GB storage + 25GB bandwidth/thang
 */
@Service("cloudinaryStorageService")
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService implements ImageStorageService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:flower-shop}")
    private String baseFolder;

    @Value("${file.allowed-extensions:jpg,jpeg,png,gif,webp}")
    private String allowedExtensions;

    /**
     * Upload file len Cloudinary
     * 
     * @param file   MultipartFile tu request
     * @param folder Thu muc con (products, categories, users)
     * @return URL cong khai cua file tren Cloudinary
     * 
     *         Vi du URL tra ve:
     *         https://res.cloudinary.com/demo/image/upload/v1234567890/flower-shop/products/abc123.jpg
     */
    @SuppressWarnings("unchecked")
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);

        try {
            // Cau hinh upload
            // public_id: duong dan tren Cloudinary (flower-shop/products/ten-file)
            // folder: thu muc chinh
            // resource_type: loai file (image, video, raw)
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", baseFolder + "/" + folder,
                    "resource_type", "image",
                    "overwrite", true,
                    "invalidate", true // Xoa cache CDN khi upload lai cung file
            );

            // Upload file len Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            // Lay URL an toan (HTTPS) tu ket qua
            String secureUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            log.info("Cloudinary upload successful. URL: {}, Public ID: {}", secureUrl, publicId);

            return secureUrl;

        } catch (IOException e) {
            log.error("Cloudinary upload failed: {}", e.getMessage());
            throw new BusinessException("CLOUDINARY_UPLOAD_ERROR",
                    "Khong the upload file len Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Upload nhieu file cung luc
     */
    public List<String> uploadFiles(MultipartFile[] files, String folder) {
        return Arrays.stream(files)
                .map(file -> uploadFile(file, folder))
                .toList();
    }

    /**
     * Xoa file tren Cloudinary theo URL
     * 
     * URL Cloudinary co dang:
     * https://res.cloudinary.com/cloud-name/image/upload/v1234567890/folder/public_id.jpg
     * 
     * Can trich xuat public_id tu URL de xoa
     */
    @SuppressWarnings("unchecked")
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }

        try {
            // Trich xuat public_id tu URL
            // VD:
            // https://res.cloudinary.com/demo/image/upload/v123/flower-shop/products/abc.jpg
            // -> public_id = flower-shop/products/abc
            String publicId = extractPublicId(fileUrl);

            if (publicId == null) {
                log.warn("Could not extract public_id from URL: {}", fileUrl);
                return false;
            }

            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String deleteResult = (String) result.get("result");

            if ("ok".equals(deleteResult)) {
                log.info("Cloudinary delete successful. Public ID: {}", publicId);
                return true;
            } else {
                log.warn("Cloudinary delete failed. Result: {}", deleteResult);
                return false;
            }

        } catch (IOException e) {
            log.error("Cloudinary delete failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Trich xuat public_id tu URL Cloudinary
     * 
     * Input:
     * https://res.cloudinary.com/demo/image/upload/v1234567890/flower-shop/products/abc.jpg
     * Output: flower-shop/products/abc
     */
    private String extractPublicId(String url) {
        try {
            // Tim vi tri /upload/ trong URL
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }

            // Lay phan sau /upload/
            String afterUpload = url.substring(uploadIndex + 8);

            // Bo phan version (v1234567890/)
            if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
                afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
            }

            // Bo phan mo rong file (.jpg, .png, ...)
            int dotIndex = afterUpload.lastIndexOf(".");
            if (dotIndex > 0) {
                afterUpload = afterUpload.substring(0, dotIndex);
            }

            return afterUpload;

        } catch (Exception e) {
            log.error("Error extracting public_id from URL: {}", url);
            return null;
        }
    }

    /**
     * Kiem tra file hop le
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("FILE_EMPTY", "File khong duoc de trong");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException("FILE_INVALID", "Ten file khong hop le");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        List<String> allowedExtList = Arrays.asList(allowedExtensions.toLowerCase().split(","));

        if (!allowedExtList.contains(extension)) {
            throw new BusinessException("FILE_TYPE_NOT_ALLOWED",
                    "Loai file khong duoc phep. Chi chap nhan: " + allowedExtensions);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("FILE_NOT_IMAGE", "File phai la hinh anh");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Tao URL voi transform (resize, crop, ...)
     * 
     * Vi du: Lay anh thumbnail 200x200
     * Original: https://res.cloudinary.com/demo/image/upload/v123/folder/image.jpg
     * Transformed:
     * https://res.cloudinary.com/demo/image/upload/c_fill,w_200,h_200/v123/folder/image.jpg
     * 
     * @param originalUrl URL goc
     * @param width       Chieu rong mong muon
     * @param height      Chieu cao mong muon
     * @return URL da transform
     */
    public String getTransformedUrl(String originalUrl, int width, int height) {
        if (originalUrl == null || !originalUrl.contains("/upload/")) {
            return originalUrl;
        }

        String transformation = String.format("c_fill,w_%d,h_%d,q_auto,f_auto", width, height);
        return originalUrl.replace("/upload/", "/upload/" + transformation + "/");
    }

    /**
     * Lay URL thumbnail nho (200x200)
     */
    public String getThumbnailUrl(String originalUrl) {
        return getTransformedUrl(originalUrl, 200, 200);
    }

    /**
     * Lay URL anh trung binh (600x600)
     */
    public String getMediumUrl(String originalUrl) {
        return getTransformedUrl(originalUrl, 600, 600);
    }
}
