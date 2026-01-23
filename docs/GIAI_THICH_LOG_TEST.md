# GIẢI THÍCH LOG KHI CHẠY UNIT TEST

Dựa vào hình ảnh bạn gửi, **XIN CHÚC MỪNG, UNIT TEST CỦA BẠN ĐÃ CHẠY THÀNH CÔNG!** ✅

Dưới đây là giải thích chi tiết ý nghĩa từng dòng thông báo để bạn tự tin giải thích với giáo viên:

## 1. Phần Chữ Đỏ (Warnings - Đừng lo lắng!)

```text
Mockito is currently self-attaching to enable the inline-mock-maker...
WARNING: A Java agent has been loaded dynamically...
```

- **Ý nghĩa:** Đây **KHÔNG PHẢI LỖI (Error)**. Đây chỉ là cảnh báo kỹ thuật của thư viện Mockito khi chạy trên các phiên bản Java mới (Java 17+).
- **Giải thích:** Mockito cần dùng một kỹ thuật gọi là "Java Agent" để có thể giả lập (Mock) các class. Java mới cảnh báo rằng "cách này sắp bị chặn trong tương lai", nhưng hiện tại **vẫn chạy tốt**.
- **Hành động:** Bạn có thể lờ nó đi. Code vẫn chạy đúng.

## 2. Phần Chữ Xanh/Trắng (INFO Logs - Bằng chứng code chạy)

Đây là phần quan trọng nhất chứng minh **Code Service của bạn đã thực sự hoạt động**.

```text
INFO com...ProductServiceImpl -- Creating product: Red Rose
```

=> **Giải thích:** Test case `create_ShouldReturnProductDTO_WhenSuccessful` đang chạy. Nó gọi hàm `create()` trong Service và dòng log `log.info("Creating product...")` đã được in ra.

```text
INFO com...ProductServiceImpl -- Getting product by ID: 1
```

=> **Giải thích:** Test case `getById_ShouldReturnProductDTO...` đang chạy. Nó gọi hàm `getById()` và log đã in ra.

```text
INFO com...ProductServiceImpl -- Created product with ID: 1
```

=> **Giải thích:** Hàm `create()` đã chạy xong dòng cuối cùng thành công mà không bị lỗi.

---

## TÓM TẮT QUY TRÌNH HOẠT ĐỘNG (CÁCH NÓ CHẠY)

Khi bạn bấm nút "Run Test", quy trình sau đã diễn ra:

1.  **Khởi động**: JUnit khởi động môi trường test.
2.  **Mocking**: `@Mock` tạo ra một `ProductRepository` **GIẢ** (Fake). Nó không kết nối Database thật.
3.  **Injection**: `@InjectMocks` lấy cái Repository giả đó nhét vào `ProductServiceImpl`.
4.  **Execution (Quan trọng nhất)**:
    - JUnit ra lệnh: "Chạy hàm `create` đi".
    - `ProductServiceImpl` chạy code thật của nó -> **In ra LOG INFO mà bạn thấy**.
    - Gặp đoạn `repository.save()`, thay vì xuống Database, nó gọi cái Repository giả ở bước 2.
    - Repository giả trả lời ngay lập tức: "Lưu xong rồi nhé" (do ta cài đặt `when(...).thenReturn(...)`).
5.  **Assertion**: JUnit kiểm tra kết quả trả về có đúng ý muốn không. Nếu đúng -> **Hiện dấu tích xanh** (hoặc kết thúc êm đẹp như hình).

**Kết luận:** Hình ảnh bạn gửi chứng minh bạn đã cài đặt Unit Test chuẩn và nó đã hoạt động chính xác!
