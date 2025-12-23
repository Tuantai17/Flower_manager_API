package com.flower.manager.service.file;

import com.flower.manager.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service xu ly upload, delete file anh - LUU TRU LOCAL
 * Luu tru trong thu muc uploads/ tren server
 * 
 * UU DIEM:
 * - Don gian, de cai dat
 * - Khong can dich vu ben ngoai
 * - Mien phi 100%
 * 
 * NHUOC DIEM:
 * - Ton dung luong server
 * - Khong co CDN -> load cham neu user o xa server
 * - Mat du lieu neu server gap su co
 */
@Service("localStorageService")
@Slf4j
public class FileStorageService implements ImageStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    @Value("${file.allowed-extensions:jpg,jpeg,png,gif,webp}")
    private String allowedExtensions;

    private Path uploadPath;
    private List<String> allowedExtList;

    /**
     * Khoi tao thu muc upload khi Service duoc tao
     */
    @PostConstruct
    public void init() {
        try {
            uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Tao cac thu muc con
            Files.createDirectories(uploadPath.resolve("products"));
            Files.createDirectories(uploadPath.resolve("categories"));
            Files.createDirectories(uploadPath.resolve("users"));

            allowedExtList = Arrays.asList(allowedExtensions.toLowerCase().split(","));

            log.info("Upload directory initialized: {}", uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    /**
     * Upload file anh cho san pham
     * 
     * @param file   MultipartFile tu request
     * @param folder Thu muc con (products, categories, users)
     * @return URL cua file da upload
     */
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("FILE_INVALID", "Ten file khong hop le");
        }
        originalFilename = StringUtils.cleanPath(originalFilename);
        String extension = getFileExtension(originalFilename);

        // Tao ten file moi voi UUID de tranh trung lap
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        try {
            Path targetFolder = uploadPath.resolve(folder);
            Files.createDirectories(targetFolder);

            Path targetLocation = targetFolder.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = baseUrl + "/" + folder + "/" + newFilename;
            log.info("File uploaded successfully: {}", fileUrl);

            return fileUrl;
        } catch (IOException e) {
            log.error("Could not store file: {}", e.getMessage());
            throw new BusinessException("FILE_UPLOAD_ERROR", "Khong the upload file: " + e.getMessage());
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
     * Xoa file theo URL
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }

        try {
            // Trich xuat duong dan file tu URL
            // VD: http://localhost:8080/uploads/products/abc.jpg -> products/abc.jpg
            String relativePath = fileUrl.replace(baseUrl + "/", "");
            Path filePath = uploadPath.resolve(relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted: {}", filePath);
                return true;
            } else {
                log.warn("File not found: {}", filePath);
                return false;
            }
        } catch (IOException e) {
            log.error("Could not delete file: {}", e.getMessage());
            return false;
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
        if (!allowedExtList.contains(extension)) {
            throw new BusinessException("FILE_TYPE_NOT_ALLOWED",
                    "Loai file khong duoc phep. Chi chap nhan: " + allowedExtensions);
        }

        // Kiem tra content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("FILE_NOT_IMAGE", "File phai la hinh anh");
        }
    }

    /**
     * Lay phan mo rong cua file
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Lay duong dan tuyet doi cua thu muc upload
     */
    public Path getUploadPath() {
        return uploadPath;
    }
}
