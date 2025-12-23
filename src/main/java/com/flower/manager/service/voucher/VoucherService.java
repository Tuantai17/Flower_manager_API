package com.flower.manager.service.voucher;

import com.flower.manager.dto.voucher.VoucherDTO;
import com.flower.manager.dto.voucher.VoucherRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface quản lý Voucher
 */
public interface VoucherService {

    /**
     * Lấy tất cả voucher - BAO GỒM cả hết hạn (Admin)
     */
    Page<VoucherDTO> getAllVouchers(String keyword, Pageable pageable);

    /**
     * Lấy danh sách voucher CÒN HIỆU LỰC cho Admin
     * - Tự động filter: còn hạn + active + còn lượt sử dụng
     */
    Page<VoucherDTO> getActiveVouchersForAdmin(String keyword, Pageable pageable);

    /**
     * Lấy danh sách voucher còn hiệu lực (Public)
     */
    List<VoucherDTO> getValidVouchers();

    /**
     * Lấy chi tiết voucher theo ID
     */
    VoucherDTO getVoucherById(Long id);

    /**
     * Lấy/Kiểm tra voucher theo mã
     */
    VoucherDTO getVoucherByCode(String code);

    /**
     * Tạo mới voucher (Admin)
     */
    VoucherDTO createVoucher(VoucherRequest request);

    /**
     * Cập nhật voucher (Admin)
     */
    VoucherDTO updateVoucher(Long id, VoucherRequest request);

    /**
     * Ẩn voucher (Soft Delete) - Đánh dấu isActive = false
     * Voucher vẫn tồn tại trong DB nhưng không hiển thị
     */
    void hideVoucher(Long id);

    /**
     * Xóa vĩnh viễn voucher (Hard Delete) - CHỈ dùng khi cần
     */
    void deleteVoucher(Long id);

    /**
     * Kích hoạt/Vô hiệu hóa voucher
     */
    VoucherDTO toggleStatus(Long id);

    /**
     * Lấy thống kê voucher
     * - activeCount: số voucher còn hiệu lực
     * - hiddenCount: số voucher đã hết hạn hoặc bị ẩn
     */
    Map<String, Long> getVoucherStats();
}
