package com.flower.manager.service.chat;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Fallback chatbot responses when Gemini API is not available
 * Contains FAQ answers and pattern matching for common questions
 */
@Component
public class ChatbotResponses {

    // Pattern -> Response mapping
    private final Map<Pattern, String> responses = new LinkedHashMap<>();

    // Quick reply suggestions
    private final List<String> defaultQuickReplies = Arrays.asList(
            "Giá hoa hồng?",
            "Thời gian giao hàng?",
            "Chính sách đổi trả?",
            "Cách đặt hàng?",
            "Liên hệ cửa hàng");

    public ChatbotResponses() {
        initResponses();
    }

    private void initResponses() {
        // Greeting patterns
        responses.put(
                Pattern.compile("(?i).*(xin chào|hello|hi|chào|hey).*"),
                "Xin chào! 🌸 Tôi là trợ lý ảo của Flower Shop. Tôi có thể giúp bạn tìm hoa, tư vấn quà tặng hoặc giải đáp thắc mắc. Bạn cần hỗ trợ gì ạ?");

        // Price inquiries
        responses.put(
                Pattern.compile("(?i).*(giá|bao nhiêu tiền|giá cả|chi phí).*hoa hồng.*"),
                "🌹 Hoa hồng của chúng tôi có nhiều mức giá:\n• Bó hồng nhỏ (5-7 bông): từ 150,000đ\n• Bó hồng trung (10-15 bông): từ 350,000đ\n• Bó hồng lớn (20+ bông): từ 500,000đ\n\nBạn muốn xem mẫu cụ thể không ạ?");

        responses.put(
                Pattern.compile("(?i).*(giá|bao nhiêu tiền|giá cả|chi phí).*"),
                "💐 Giá hoa của chúng tôi dao động từ 100,000đ - 2,000,000đ tùy loại:\n• Hoa hồng: từ 150,000đ\n• Hoa cúc: từ 100,000đ\n• Hoa ly: từ 200,000đ\n• Hoa lan: từ 300,000đ\n\nBạn quan tâm loại hoa nào ạ?");

        // Delivery
        responses.put(
                Pattern.compile("(?i).*(giao hàng|ship|vận chuyển|thời gian giao|bao lâu).*"),
                "🚚 Thông tin giao hàng:\n• Nội thành: 2-4 tiếng\n• Ngoại thành: 4-8 tiếng\n• Đặt trước: Giao đúng giờ hẹn\n• Phí ship: 15,000đ - 30,000đ\n• FREE ship đơn từ 500,000đ\n\nBạn cần giao đến khu vực nào ạ?");

        // Return policy
        responses.put(
                Pattern.compile("(?i).*(đổi trả|hoàn tiền|bảo hành|hư hỏng|khiếu nại).*"),
                "🔄 Chính sách đổi trả:\n• Đổi miễn phí trong 2 giờ nếu hoa hư hỏng\n• Hoàn 100% nếu lỗi từ shop\n• Hỗ trợ đổi mẫu nếu còn hàng\n• Liên hệ hotline: 1900-xxxx\n\nBạn cần hỗ trợ vấn đề gì ạ?");

        // How to order
        responses.put(
                Pattern.compile("(?i).*(cách đặt|đặt hàng|mua hàng|order|đặt mua|hướng dẫn đặt).*"),
                "📝 Cách đặt hàng:\n1. Chọn hoa yêu thích trong danh mục\n2. Thêm vào giỏ hàng\n3. Điền thông tin người nhận\n4. Chọn thời gian giao\n5. Thanh toán (COD hoặc MoMo)\n\nBạn cần tôi hướng dẫn chi tiết hơn không ạ?");

        // Payment
        responses.put(
                Pattern.compile("(?i).*(thanh toán|payment|trả tiền|cod|momo).*"),
                "💳 Phương thức thanh toán:\n• COD - Thanh toán khi nhận hàng\n• MoMo - Thanh toán online\n• Chuyển khoản ngân hàng\n\nTất cả đều an toàn và được bảo mật!");

        // Occasions
        responses.put(
                Pattern.compile("(?i).*(sinh nhật|birthday|kỷ niệm|valentine|8/3|20/10|lễ tình nhân).*"),
                "🎉 Hoa cho dịp đặc biệt:\n• Sinh nhật: Bó hoa hồng mix, hoa hướng dương\n• Valentine/Lễ tình nhân: Hoa hồng đỏ, hộp hoa tim\n• 8/3, 20/10: Bó hoa mix, hoa ly\n• Kỷ niệm: Hoa lan, hoa cao cấp\n\nTôi có thể gợi ý mẫu cụ thể cho bạn!");

        // Product recommendations
        responses.put(
                Pattern.compile("(?i).*(gợi ý|tư vấn|nên mua|recommend|đề xuất).*"),
                "💐 Một số gợi ý hot nhất:\n• Bó hồng Ecuador - Sang trọng\n• Bó hướng dương - Tươi vui\n• Hộp hoa mix - Tinh tế\n• Lẵng hoa chúc mừng - Trang trọng\n\nBạn muốn tặng ai và dịp gì để tôi tư vấn phù hợp hơn ạ?");

        // Contact info
        responses.put(
                Pattern.compile("(?i).*(liên hệ|contact|hotline|số điện thoại|địa chỉ|email).*"),
                "📞 Thông tin liên hệ Flower Shop:\n• Hotline: 1900-xxxx (8h-22h)\n• Email: support@flowershop.vn\n• Facebook: fb.com/flowershop\n• Địa chỉ: 123 Đường Hoa, Q.1, TP.HCM\n\nBạn có thể liên hệ bất cứ lúc nào!");

        // Want staff support
        responses.put(
                Pattern.compile("(?i).*(nhân viên|tư vấn viên|người thật|support|hỗ trợ viên).*"),
                "👨‍💼 Bạn muốn nói chuyện với nhân viên hỗ trợ? Nhấn nút \"Cần nhân viên hỗ trợ\" bên dưới để được kết nối với tư vấn viên của chúng tôi nhé!");

        // Store hours
        responses.put(
                Pattern.compile("(?i).*(giờ mở cửa|thời gian làm việc|mấy giờ|khi nào mở).*"),
                "⏰ Thời gian hoạt động:\n• Thứ 2 - Thứ 6: 7h00 - 21h00\n• Thứ 7 - Chủ nhật: 8h00 - 22h00\n• Ngày lễ: 8h00 - 20h00\n\nĐặt hàng online 24/7!");

        // Thanks
        responses.put(
                Pattern.compile("(?i).*(cảm ơn|thank|thanks|tks).*"),
                "Không có gì ạ! 🌸 Rất vui được hỗ trợ bạn. Nếu cần thêm thông tin gì, đừng ngại hỏi tôi nhé! Chúc bạn một ngày tuyệt vời! 💐");

        // Goodbye
        responses.put(
                Pattern.compile("(?i).*(tạm biệt|bye|goodbye|hẹn gặp lại).*"),
                "Tạm biệt bạn! 👋 Cảm ơn bạn đã ghé thăm Flower Shop. Hẹn gặp lại bạn lần sau! 🌸💐");
    }

    /**
     * Get response for user message
     * 
     * @param message User message
     * @return Bot response or null if no match
     */
    public String getResponse(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Xin lỗi, tôi không hiểu. Bạn có thể nói rõ hơn được không ạ?";
        }

        for (Map.Entry<Pattern, String> entry : responses.entrySet()) {
            if (entry.getKey().matcher(message).matches()) {
                return entry.getValue();
            }
        }

        return null; // No pattern matched
    }

    /**
     * Get default response when nothing matches
     */
    public String getDefaultResponse() {
        return "Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp";
    }

    /**
     * Get quick reply suggestions
     */
    public List<String> getQuickReplies() {
        return defaultQuickReplies;
    }

    /**
     * Get welcome message for new session
     */
    public String getWelcomeMessage() {
        return "Xin chào! 🌸 Tôi là trợ lý ảo của Flower Shop.\n\nTôi có thể giúp bạn:\n• Tìm kiếm và tư vấn hoa\n• Thông tin giao hàng, thanh toán\n• Chính sách đổi trả\n\nBạn cần hỗ trợ gì ạ?";
    }

    /**
     * Get staff request message
     */
    public String getStaffRequestMessage() {
        return "📞 Yêu cầu của bạn đã được ghi nhận!\n\nNhân viên hỗ trợ sẽ phản hồi trong vòng vài phút. Trong khi chờ đợi, bạn có thể tiếp tục đặt câu hỏi và tôi sẽ cố gắng hỗ trợ bạn.";
    }
}
