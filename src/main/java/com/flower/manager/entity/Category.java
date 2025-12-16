package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity Category - Hỗ trợ cấu trúc đa cấp 2 lớp (Cha - Con)
 * Ví dụ:
 * - Hoa Tươi (parent = null) -> Hoa Hồng, Hoa Lan, Hoa Cúc (parent = Hoa Tươi)
 * - Cây Cảnh (parent = null) -> Cây Bonsai, Cây Phong Thủy (parent = Cây Cảnh)
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String imageUrl;

    /**
     * Quan hệ cha - con (Self-referencing)
     * parent = null -> Danh mục cấp 1 (cha)
     * parent != null -> Danh mục cấp 2 (con)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * Danh sách các danh mục con
     * mappedBy = "parent" -> liên kết với field parent ở trên
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Integer sortOrder = 0;

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
     * Kiểm tra có phải danh mục cha không (cấp 1)
     */
    public boolean isParentCategory() {
        return parent == null;
    }

    /**
     * Kiểm tra có phải danh mục con không (cấp 2)
     */
    public boolean isChildCategory() {
        return parent != null;
    }

    /**
     * Thêm danh mục con
     */
    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Xóa danh mục con
     */
    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }
}
