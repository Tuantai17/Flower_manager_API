package com.flower.manager.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Factory de chon Service luu tru anh phu hop
 * Dua tren cau hinh file.storage-mode trong application.properties
 * 
 * CACH SU DUNG:
 * =============
 * 1. Trong application.properties:
 * file.storage-mode=local -> Su dung Local Storage
 * file.storage-mode=cloudinary -> Su dung Cloudinary
 * 
 * 2. Inject ImageStorageFactory vao Controller/Service can dung:
 * 
 * @Autowired
 *            private ImageStorageFactory storageFactory;
 * 
 *            public void upload(MultipartFile file) {
 *            String url = storageFactory.getService().uploadFile(file,
 *            "products");
 *            }
 * 
 *            UU DIEM CUA FACTORY PATTERN:
 *            ============================
 *            - De dang chuyen doi giua Local va Cloudinary chi bang thay doi
 *            config
 *            - Khong can sua code khi muon doi loai storage
 *            - De mo rong them cac loai storage khac (AWS S3, Google Cloud,
 *            ...)
 */
@Component
@Slf4j
public class ImageStorageFactory {

    private final ImageStorageService localStorageService;
    private final ImageStorageService cloudinaryStorageService;
    private final ImageStorageService dualStorageService;

    @Value("${file.storage-mode:local}")
    private String storageMode;

    public ImageStorageFactory(
            @Qualifier("localStorageService") ImageStorageService localStorageService,
            @Qualifier("cloudinaryStorageService") ImageStorageService cloudinaryStorageService,
            @Qualifier("dualStorageService") ImageStorageService dualStorageService) {
        this.localStorageService = localStorageService;
        this.cloudinaryStorageService = cloudinaryStorageService;
        this.dualStorageService = dualStorageService;
    }

    /**
     * Tra ve Service phu hop dua tren cau hinh
     * 
     * @return ImageStorageService (Local, Cloudinary hoac Dual)
     */
    public ImageStorageService getService() {
        if ("cloudinary".equalsIgnoreCase(storageMode)) {
            log.debug("Using Cloudinary storage");
            return cloudinaryStorageService;
        } else if ("dual".equalsIgnoreCase(storageMode)) {
            log.debug("Using Dual storage (Local + Cloudinary)");
            return dualStorageService;
        } else {
            log.debug("Using Local storage");
            return localStorageService;
        }
    }

    /**
     * Kiem tra dang su dung loai storage nao
     */
    public boolean isCloudinaryMode() {
        return "cloudinary".equalsIgnoreCase(storageMode);
    }

    /**
     * Kiem tra dang su dung che do Dual
     */
    public boolean isDualMode() {
        return "dual".equalsIgnoreCase(storageMode);
    }

    /**
     * Lay truc tiep Local Storage Service
     */
    public ImageStorageService getLocalService() {
        return localStorageService;
    }

    /**
     * Lay truc tiep Cloudinary Service
     */
    public ImageStorageService getCloudinaryService() {
        return cloudinaryStorageService;
    }
}
