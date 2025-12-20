package com.flower.manager.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class cho việc xử lý Slug
 * Hỗ trợ chuyển đổi tiếng Việt sang slug chuẩn
 */
public final class SlugUtils {

    private SlugUtils() {
        // Prevent instantiation
    }

    /**
     * Chuyển đổi text thành slug chuẩn
     * Ví dụ: "Hoa Hồng Đỏ" -> "hoa-hong-do"
     * 
     * @param text Text cần chuyển đổi
     * @return Slug chuẩn (lowercase, no accents, hyphen-separated)
     */
    public static String toSlug(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        // Chuyển về lowercase
        String result = text.toLowerCase().trim();

        // Thay thế các ký tự tiếng Việt
        result = removeVietnameseAccents(result);

        // Thay thế các ký tự đặc biệt bằng dấu gạch ngang
        result = result.replaceAll("[^a-z0-9\\s-]", "");

        // Thay thế khoảng trắng và nhiều dấu gạch ngang liên tiếp bằng một dấu gạch
        // ngang
        result = result.replaceAll("[\\s-]+", "-");

        // Xóa dấu gạch ngang ở đầu và cuối
        result = result.replaceAll("^-+|-+$", "");

        return result;
    }

    /**
     * Chuyển đổi text thành slug với suffix (để tránh trùng lặp)
     * Ví dụ: "Hoa Hồng" với suffix "2" -> "hoa-hong-2"
     * 
     * @param text   Text cần chuyển đổi
     * @param suffix Suffix thêm vào cuối
     * @return Slug với suffix
     */
    public static String toSlugWithSuffix(String text, String suffix) {
        String slug = toSlug(text);
        if (suffix != null && !suffix.isBlank()) {
            return slug + "-" + suffix;
        }
        return slug;
    }

    /**
     * Loại bỏ dấu tiếng Việt
     * 
     * @param text Text cần xử lý
     * @return Text không dấu
     */
    public static String removeVietnameseAccents(String text) {
        if (text == null) {
            return null;
        }

        // Normalize để tách dấu ra khỏi ký tự
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);

        // Pattern để match các ký tự combining diacritical marks
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(normalized).replaceAll("");

        // Xử lý các ký tự đặc biệt tiếng Việt
        result = result.replace('đ', 'd').replace('Đ', 'D');

        return result;
    }

    /**
     * Kiểm tra slug có hợp lệ không
     * Slug hợp lệ chỉ chứa chữ thường, số và dấu gạch ngang
     * 
     * @param slug Slug cần kiểm tra
     * @return true nếu hợp lệ
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            return false;
        }
        return slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$");
    }

    /**
     * Sinh slug unique bằng cách thêm timestamp
     * 
     * @param text Text cần chuyển đổi
     * @return Slug unique với timestamp
     */
    public static String toUniqueSlug(String text) {
        String slug = toSlug(text);
        long timestamp = System.currentTimeMillis() % 100000; // Lấy 5 số cuối
        return slug + "-" + timestamp;
    }
}
