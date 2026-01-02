package com.flower.manager.controller.voucher;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.voucher.SavedVoucherDTO;
import com.flower.manager.service.voucher.SavedVoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller cho Saved Vouchers (Kho Voucher của User)
 * 
 * Endpoints:
 * POST /api/vouchers/save/{voucherId} : Lưu voucher vào kho
 * DELETE /api/vouchers/unsave/{voucherId} : Xóa voucher khỏi kho
 * GET /api/vouchers/check-saved/{id} : Kiểm tra đã lưu chưa
 * GET /api/vouchers/my-vouchers : Lấy tất cả voucher đã lưu
 * GET /api/vouchers/my-vouchers/filter : Lấy theo filter (available, expiring,
 * expired, used)
 * GET /api/vouchers/my-vouchers/counts : Đếm số lượng theo loại
 */
@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Slf4j
public class SavedVoucherController {

    private final SavedVoucherService savedVoucherService;

    /**
     * Lưu voucher vào kho của user
     * POST /api/vouchers/save/{voucherId}
     */
    @PostMapping("/save/{voucherId}")
    public ResponseEntity<ApiResponse<SavedVoucherDTO>> saveVoucher(@PathVariable Long voucherId) {
        log.info("Saving voucher {} to user's wallet", voucherId);
        SavedVoucherDTO saved = savedVoucherService.saveVoucher(voucherId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(saved, "Đã lưu voucher vào kho"));
    }

    /**
     * Xóa voucher khỏi kho
     * DELETE /api/vouchers/unsave/{voucherId}
     */
    @DeleteMapping("/unsave/{voucherId}")
    public ResponseEntity<ApiResponse<Void>> unsaveVoucher(@PathVariable Long voucherId) {
        log.info("Removing voucher {} from user's wallet", voucherId);
        savedVoucherService.unsaveVoucher(voucherId);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa voucher khỏi kho"));
    }

    /**
     * Kiểm tra user đã lưu voucher chưa
     * GET /api/vouchers/check-saved/{voucherId}
     */
    @GetMapping("/check-saved/{voucherId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkVoucherSaved(@PathVariable Long voucherId) {
        boolean isSaved = savedVoucherService.isVoucherSaved(voucherId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("isSaved", isSaved)));
    }

    /**
     * Lấy tất cả voucher đã lưu của user
     * GET /api/vouchers/my-vouchers
     */
    @GetMapping("/my-vouchers")
    public ResponseEntity<ApiResponse<List<SavedVoucherDTO>>> getMySavedVouchers() {
        log.info("Fetching user's saved vouchers");
        List<SavedVoucherDTO> vouchers = savedVoucherService.getMySavedVouchers();
        return ResponseEntity.ok(ApiResponse.success(vouchers, "Lấy kho voucher thành công"));
    }

    /**
     * Lấy voucher theo filter
     * GET /api/vouchers/my-vouchers/filter?type=available|expiring|expired|used
     */
    @GetMapping("/my-vouchers/filter")
    public ResponseEntity<ApiResponse<List<SavedVoucherDTO>>> getMyVouchersByFilter(
            @RequestParam(defaultValue = "all") String type) {
        log.info("Fetching user's vouchers with filter: {}", type);
        List<SavedVoucherDTO> vouchers = savedVoucherService.getMyVouchersByFilter(type);
        return ResponseEntity.ok(ApiResponse.success(vouchers, "Lấy voucher theo filter thành công"));
    }

    /**
     * Lấy voucher còn dùng được (cho checkout)
     * GET /api/vouchers/my-vouchers/available
     */
    @GetMapping("/my-vouchers/available")
    public ResponseEntity<ApiResponse<List<SavedVoucherDTO>>> getMyAvailableVouchers() {
        log.info("Fetching user's available vouchers for checkout");
        List<SavedVoucherDTO> vouchers = savedVoucherService.getMyAvailableVouchers();
        return ResponseEntity.ok(ApiResponse.success(vouchers, "Lấy voucher khả dụng thành công"));
    }

    /**
     * Đếm số lượng voucher theo loại
     * GET /api/vouchers/my-vouchers/counts
     */
    @GetMapping("/my-vouchers/counts")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getVoucherCounts() {
        Map<String, Long> counts = savedVoucherService.getVoucherCounts();
        return ResponseEntity.ok(ApiResponse.success(counts));
    }
}
