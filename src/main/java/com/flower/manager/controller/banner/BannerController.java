package com.flower.manager.controller.banner;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.banner.BannerDTO;
import com.flower.manager.service.banner.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BannerController {

    private final BannerService bannerService;

    // ============ PUBLIC API ============

    /**
     * Lấy danh sách banner active (theo thời gian và trạng thái)
     * GET /api/banners
     */
    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<List<BannerDTO>>> getPublicBanners() {
        return ResponseEntity.ok(ApiResponse.success(bannerService.getActiveBanners()));
    }

    // ============ ADMIN API ============

    /**
     * Lấy tất cả banner (admin)
     * GET /api/admin/banners
     */
    @GetMapping("/admin/banners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BannerDTO>>> getAllBanners() {
        return ResponseEntity.ok(ApiResponse.success(bannerService.getAllBanners()));
    }

    /**
     * Lấy banner theo ID
     * GET /api/admin/banners/{id}
     */
    @GetMapping("/admin/banners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerDTO>> getBannerById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.getBannerById(id)));
    }

    /**
     * Tạo banner mới
     * POST /api/admin/banners
     * 
     * Body: {
     * "title": "Banner chào mừng",
     * "imageUrl": "https://cloudinary.com/...",
     * "linkUrl": "/products",
     * "sortOrder": 1,
     * "active": true,
     * "startDate": "2024-01-01T00:00:00",
     * "endDate": "2024-12-31T23:59:59",
     * "description": "Mô tả"
     * }
     */
    @PostMapping("/admin/banners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerDTO>> createBanner(@Valid @RequestBody BannerDTO bannerDTO) {
        BannerDTO created = bannerService.createBanner(bannerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "Tạo banner thành công"));
    }

    /**
     * Cập nhật banner
     * PUT /api/admin/banners/{id}
     */
    @PutMapping("/admin/banners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerDTO>> updateBanner(
            @PathVariable Long id,
            @Valid @RequestBody BannerDTO bannerDTO) {
        BannerDTO updated = bannerService.updateBanner(id, bannerDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật banner thành công"));
    }

    /**
     * Xóa banner
     * DELETE /api/admin/banners/{id}
     */
    @DeleteMapping("/admin/banners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa banner thành công"));
    }

    /**
     * Bật/tắt banner
     * PATCH /api/admin/banners/{id}/active
     */
    @PatchMapping("/admin/banners/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerDTO>> toggleActive(@PathVariable Long id) {
        BannerDTO updated = bannerService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success(updated, "Thay đổi trạng thái banner thành công"));
    }

    /**
     * Cập nhật thứ tự banner hàng loạt
     * PATCH /api/admin/banners/reorder
     * 
     * Body: {
     * "1": 0,
     * "2": 1,
     * "3": 2
     * }
     */
    @PatchMapping("/admin/banners/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> reorderBanners(@RequestBody Map<Long, Integer> orderMap) {
        bannerService.reorderBanners(orderMap);
        return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật thứ tự banner thành công"));
    }
}
