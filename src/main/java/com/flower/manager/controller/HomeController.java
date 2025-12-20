package com.flower.manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller xử lý root path và các endpoint thông tin API
 * Giải quyết lỗi NoResourceFoundException khi truy cập "/"
 */
@RestController
public class HomeController {

    /**
     * Root endpoint - Hiển thị thông tin API
     * GET /
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("application", "Flower Manager API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("products", "GET /api/products - Lấy danh sách sản phẩm");
        endpoints.put("categories", "GET /api/categories - Lấy danh sách danh mục");
        endpoints.put("categoriesMenu", "GET /api/categories/menu - Lấy menu danh mục");
        endpoints.put("auth", "POST /api/auth/login, /api/auth/register");
        response.put("publicEndpoints", endpoints);

        return ResponseEntity.ok(response);
    }

    /**
     * API info endpoint
     * GET /api
     */
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", "Flower Manager API");
        response.put("version", "1.0.0");
        response.put("description", "REST API for Flower Shop Management System");

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     * GET /health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return ResponseEntity.ok(response);
    }
}
