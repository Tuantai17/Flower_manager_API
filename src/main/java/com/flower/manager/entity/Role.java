package com.flower.manager.entity;

/**
 * Enum các vai trò người dùng trong hệ thống
 * - ADMIN: Quản trị viên - có toàn quyền quản lý
 * - STAFF: Nhân viên - có quyền quản lý sản phẩm, đơn hàng
 * - CUSTOMER: Khách hàng - chỉ mua hàng và xem đơn của mình
 */
public enum Role {
    ADMIN,
    STAFF,
    CUSTOMER
}
