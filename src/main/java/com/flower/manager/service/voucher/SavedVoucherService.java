package com.flower.manager.service.voucher;

import com.flower.manager.dto.voucher.SavedVoucherDTO;

import java.util.List;
import java.util.Map;

/**
 * Service interface cho Saved Vouchers (Kho Voucher của User)
 */
public interface SavedVoucherService {

    /**
     * Lưu voucher vào kho của user
     */
    SavedVoucherDTO saveVoucher(Long voucherId);

    /**
     * Xóa voucher khỏi kho
     */
    void unsaveVoucher(Long voucherId);

    /**
     * Kiểm tra user đã lưu voucher chưa
     */
    boolean isVoucherSaved(Long voucherId);

    /**
     * Lấy tất cả voucher đã lưu của user hiện tại
     */
    List<SavedVoucherDTO> getMySavedVouchers();

    /**
     * Lấy voucher còn dùng được (chưa sử dụng + còn hạn)
     */
    List<SavedVoucherDTO> getMyAvailableVouchers();

    /**
     * Lấy voucher sắp hết hạn (trong 3 ngày)
     */
    List<SavedVoucherDTO> getMyExpiringVouchers();

    /**
     * Lấy voucher đã hết hạn
     */
    List<SavedVoucherDTO> getMyExpiredVouchers();

    /**
     * Lấy voucher đã sử dụng
     */
    List<SavedVoucherDTO> getMyUsedVouchers();

    /**
     * Lấy voucher theo filter
     * 
     * @param filter: all, available, expiring, expired, used
     */
    List<SavedVoucherDTO> getMyVouchersByFilter(String filter);

    /**
     * Đánh dấu voucher đã sử dụng (gọi khi đặt hàng thành công)
     */
    void markVoucherAsUsed(Long userId, Long voucherId);

    /**
     * Đếm số lượng voucher theo từng loại
     */
    Map<String, Long> getVoucherCounts();
}
