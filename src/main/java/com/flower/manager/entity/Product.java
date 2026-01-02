package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity Product - Sản phẩm hoa
 * Liên kết với Category (N-1)
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 15, scale = 2)
    private BigDecimal salePrice;

    @Column(length = 500)
    private String thumbnail;

    @Builder.Default
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    /**
     * Trạng thái sản phẩm:
     * 1 = Active (đang bán)
     * 0 = Inactive (ngừng bán)
     * 2 = Out of stock (hết hàng)
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer status = 1;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Quan hệ Many-to-One với Category
     * Sản phẩm thuộc về một danh mục
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Mã SKU sản phẩm
     */
    @Column(name = "sku", length = 100)
    private String sku;

    /**
     * Số lượng đã bán
     */
    @Builder.Default
    @Column(name = "sold_count")
    private Integer soldCount = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ============ Helper Methods ============

    /**
     * Kiểm tra sản phẩm có đang giảm giá không
     */
    public boolean isOnSale() {
        return salePrice != null && salePrice.compareTo(BigDecimal.ZERO) > 0 && salePrice.compareTo(price) < 0;
    }

    /**
     * Lấy giá hiện tại (giá sale nếu có, không thì giá gốc)
     */
    public BigDecimal getCurrentPrice() {
        return isOnSale() ? salePrice : price;
    }

    /**
     * Kiểm tra còn hàng không
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Kiểm tra sản phẩm có active không
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(active) && status == 1;
    }
}
