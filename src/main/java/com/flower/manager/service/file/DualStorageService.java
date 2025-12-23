package com.flower.manager.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service luu tru song song (Dual Storage)
 * Luu dong thoi len ca Local Storage va Cloudinary
 * 
 * CACH HOAT DONG:
 * ===============
 * 1. Khi upload: Chay song song (Parallel) viec upload len Local va Cloudinary
 * 2. Su dung CompletableFuture de toi uu toc do (khong doi nhau)
 * 3. Tra ve URL cua Cloudinary lam URL chinh (vi co CDN load nhanh)
 * 4. Local copy dong vai tro la Backup du lieu
 */
@Service("dualStorageService")
@Slf4j
public class DualStorageService implements ImageStorageService {

    private final ImageStorageService localStorageService;
    private final ImageStorageService cloudinaryStorageService;

    public DualStorageService(
            @Qualifier("localStorageService") ImageStorageService localStorageService,
            @Qualifier("cloudinaryStorageService") ImageStorageService cloudinaryStorageService) {
        this.localStorageService = localStorageService;
        this.cloudinaryStorageService = cloudinaryStorageService;
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        log.info("Dual upload starting for file: {}", file.getOriginalFilename());

        // Chay song song 2 tac vu upload
        CompletableFuture<String> localUpload = CompletableFuture
                .supplyAsync(() -> localStorageService.uploadFile(file, folder));

        CompletableFuture<String> cloudinaryUpload = CompletableFuture
                .supplyAsync(() -> cloudinaryStorageService.uploadFile(file, folder));

        // Doi ca 2 hoan thanh
        CompletableFuture.allOf(localUpload, cloudinaryUpload).join();

        try {
            String cloudinaryUrl = cloudinaryUpload.get();
            log.info("Dual upload successful. Primary URL (Cloudinary): {}", cloudinaryUrl);
            return cloudinaryUrl;
        } catch (Exception e) {
            log.error("Error getting upload results: {}", e.getMessage());
            // Neu Cloudinary loi nhung Local thanh cong, van tra ve Local URL de app khong
            // chet
            return localUpload.join();
        }
    }

    @Override
    public List<String> uploadFiles(MultipartFile[] files, String folder) {
        // De don gian, thuc hien tung file qua ham uploadFile da co logic parallel
        return java.util.Arrays.stream(files)
                .map(file -> uploadFile(file, folder))
                .toList();
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        log.info("Dual delete starting for URL: {}", fileUrl);

        // Chay song song viec xoa o ca 2 noi
        CompletableFuture<Boolean> localDelete = CompletableFuture
                .supplyAsync(() -> localStorageService.deleteFile(fileUrl));

        CompletableFuture<Boolean> cloudinaryDelete = CompletableFuture
                .supplyAsync(() -> cloudinaryStorageService.deleteFile(fileUrl));

        // Doi ca 2 hoan thanh
        CompletableFuture.allOf(localDelete, cloudinaryDelete).join();

        return true; // Luon tra ve true vi day la tac vu cleanup
    }
}
