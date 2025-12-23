package com.flower.manager.controller.voucher;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.voucher.VoucherDTO;
import com.flower.manager.dto.voucher.VoucherRequest;
import com.flower.manager.service.voucher.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller quản lý Voucher
 * 
 * Endpoint: /api/vouchers/**
 * 
 * Quy tắc hiển thị:
 * - Admin mặc định chỉ thấy voucher CÒN HIỆU LỰC
 * - Voucher hết hạn/bị ẩn sẽ không hiển thị trong danh sách mặc định
 * - Admin có thể chọn xem TẤT CẢ voucher (bao gồm hết hạn)
 */
@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Slf4j
public class VoucherController {

    private final VoucherService voucherService;

    // ================= PUBLIC ENDPOINTS =================

    /**
     * Lấy danh sách voucher còn hiệu lực (cho khách hàng)
     * GET /api/vouchers/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> getActiveVouchers() {
        List<VoucherDTO> vouchers = voucherService.getValidVouchers();
        return ResponseEntity.ok(ApiResponse.success(vouchers,
                "Danh sách voucher còn hiệu lực"));
    }

    /**
     * Kiểm tra mã voucher
     * GET /api/vouchers/check/{code}
     */
    @GetMapping("/check/{code}")
    public ResponseEntity<ApiResponse<VoucherDTO>> checkVoucher(@PathVariable String code) {
        VoucherDTO voucher = voucherService.getVoucherByCode(code);
        if (voucher.getIsValid()) {
            return ResponseEntity.ok(ApiResponse.success(voucher, "Mã giảm giá hợp lệ"));
        } else {
            return ResponseEntity.ok(ApiResponse.success(voucher,
                    "Mã giảm giá đã hết hạn hoặc không còn hiệu lực"));
        }
    }

    // ================= ADMIN ENDPOINTS =================

    /**
     * Lấy voucher CÒN HIỆU LỰC cho Admin (MẶC ĐỊNH)
     * - Tự động filter: chỉ hiển thị voucher còn hạn + active + còn lượt
     * 
     * GET /api/vouchers/admin
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<VoucherDTO>>> getActiveVouchersForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VoucherDTO> vouchers = voucherService.getActiveVouchersForAdmin(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(vouchers,
                "Chỉ hiển thị voucher còn hạn"));
    }

    /**
     * Lấy TẤT CẢ voucher (bao gồm hết hạn + bị ẩn) cho Admin
     * 
     * GET /api/vouchers/admin/all
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<VoucherDTO>>> getAllVouchers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VoucherDTO> vouchers = voucherService.getAllVouchers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(vouchers,
                "Hiển thị tất cả voucher (bao gồm hết hạn và đã ẩn)"));
    }

    /**
     * Lấy thống kê voucher
     * GET /api/vouchers/admin/stats
     * 
     * Response: { activeCount, hiddenCount, totalCount }
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getVoucherStats() {
        Map<String, Long> stats = voucherService.getVoucherStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Thống kê voucher"));
    }

    /**
     * Tạo mới voucher
     * POST /api/vouchers/admin/create
     */
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VoucherDTO>> createVoucher(@Valid @RequestBody VoucherRequest request) {
        VoucherDTO voucher = voucherService.createVoucher(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(voucher, "Tạo voucher thành công"));
    }

    /**
     * Cập nhật voucher
     * PUT /api/vouchers/admin/{id}
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VoucherDTO>> updateVoucher(
            @PathVariable Long id,
            @Valid @RequestBody VoucherRequest request) {
        VoucherDTO voucher = voucherService.updateVoucher(id, request);
        return ResponseEntity.ok(ApiResponse.success(voucher, "Cập nhật voucher thành công"));
    }

    /**
     * ẨN voucher (Soft Delete)
     * - Voucher bị ẩn khỏi danh sách mặc định
     * - Vẫn tồn tại trong DB, có thể khôi phục bằng toggleStatus
     * 
     * DELETE /api/vouchers/admin/{id}
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> hideVoucher(@PathVariable Long id) {
        voucherService.hideVoucher(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã ẩn voucher"));
    }

    /**
     * Xóa VĨNH VIỄN voucher (Hard Delete) - KHÔNG KHUYẾN KHÍCH
     * - Chỉ dùng khi thực sự cần xóa hẳn
     * - Có thể gây lỗi nếu voucher đã được sử dụng trong đơn hàng
     * 
     * DELETE /api/vouchers/admin/{id}/permanent
     */
    @DeleteMapping("/admin/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVoucherPermanently(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa vĩnh viễn voucher"));
    }

    /**
     * Bật/Tắt trạng thái voucher
     * - Có thể dùng để khôi phục voucher đã bị ẩn
     * 
     * PATCH /api/vouchers/admin/{id}/toggle
     */
    @PatchMapping("/admin/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VoucherDTO>> toggleStatus(@PathVariable Long id) {
        VoucherDTO voucher = voucherService.toggleStatus(id);
        String message = voucher.getIsActive()
                ? "Đã kích hoạt voucher"
                : "Đã ẩn voucher";
        return ResponseEntity.ok(ApiResponse.success(voucher, message));
    }

    /**
     * Lấy chi tiết voucher theo ID
     * GET /api/vouchers/admin/{id}
     */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VoucherDTO>> getVoucherById(@PathVariable Long id) {
        VoucherDTO voucher = voucherService.getVoucherById(id);
        return ResponseEntity.ok(ApiResponse.success(voucher));
    }
}
