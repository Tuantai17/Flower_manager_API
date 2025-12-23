package com.flower.manager.service.file;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * Interface chung cho cac Service luu tru file
 * Cho phep chuyen doi giua Local Storage va Cloudinary
 */
public interface ImageStorageService {

    /**
     * Upload 1 file anh
     * 
     * @param file   MultipartFile
     * @param folder Thu muc con (products, categories, users)
     * @return URL cua file sau khi upload
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * Upload nhieu file cung luc
     */
    List<String> uploadFiles(MultipartFile[] files, String folder);

    /**
     * Xoa file theo URL
     * 
     * @return true neu xoa thanh cong
     */
    boolean deleteFile(String fileUrl);
}
