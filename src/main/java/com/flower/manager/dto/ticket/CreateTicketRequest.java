package com.flower.manager.dto.ticket;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO tạo ticket mới
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 255, message = "Họ tên tối đa 255 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @Size(max = 255, message = "Chủ đề tối đa 255 ký tự")
    private String subject;

    private String category; // ORDER, SUPPORT, FEEDBACK, PARTNERSHIP, OTHER

    @NotBlank(message = "Nội dung không được để trống")
    private String message;
}
