# LÝ THUYẾT CHI TIẾT VỀ UNIT TEST (KIỂM THỬ ĐƠN VỊ)

**Tài liệu ôn tập trả lời câu hỏi lý thuyết**

---

## 1. Unit Test là gì?

**Định nghĩa:**
Unit Test (Kiểm thử đơn vị) là mức độ kiểm thử nhỏ nhất trong quy trình kiểm thử phần mềm. Trong đó, các **"đơn vị" (units)** nhỏ nhất của mã nguồn được kiểm tra một cách riêng biệt và độc lập.

- Trong Java/Spring Boot, một "Unit" thường là một **Method (Hàm)** hoặc một **Class**.
- Mục tiêu: Đảm bảo từng đoạn code nhỏ hoạt động đúng như thiết kế trước khi ghép chúng lại với nhau.

---

## 2. Unit Test dùng để làm gì? (Mục đích)

Mục đích chính không chỉ là "tìm lỗi", mà sâu xa hơn là:

1.  **Cô lập lỗi (Isolate bugs):** Khi test thất bại, bạn biết chính xác lỗi nằm ở hàm nào, dòng nào. (Khác với chạy cả ứng dụng lên rồi lỗi, lúc đó rất khó biết lỗi ở đâu).
2.  **Đảm bảo logic nghiệp vụ (Validate Logic):** Kiểm chứng rằng logic if-else, tính toán cộng trừ nhân chia của bạn là đúng với mọi trường hợp input.
3.  **Tự tin khi Refactor (Sửa code):** Khi bạn muốn tối ưu code cũ, nếu có Unit Test, bạn cứ thoải mái sửa. Sau đó chạy lại Test, nếu vẫn xanh (Passed) thì nghĩa là logic chưa bị phá vỡ.
4.  **Tài liệu sống (Documentation):** Nhìn vào Unit Test, người khác sẽ hiểu hàm này nhận input gì và trả về output gì.

---

## 3. Chức năng và Lợi ích cụ thể

### A. Phát hiện lỗi sớm (Early Bug Detection)

Phát hiện lỗi ngay từ khi viết code (Development phase), thay vì đợi đến khi Tester kiểm tra hoặc tệ hơn là khi khách hàng sử dụng mới phát hiện.

- _Lợi ích_: Chi phí sửa lỗi thấp hơn rất nhiều.

### B. Thay đổi code an toàn (Safe Refactoring)

Ví dụ: Bạn có hàm `tinhTong(a, b)`. Bạn đã viết test `tinhTong(1, 2) = 3`.
Sau này bạn sửa lại cách implement hàm `tinhTong` cho nhanh hơn. Bạn chạy lại test, thấy vẫn ra 3 -> Yên tâm code mới đúng. Nếu ra 4 -> Code mới sai, undo lại ngay.

### C. Đơn giản hóa việc tích hợp (Simpler Integration)

Nếu từng viên gạch (Unit) đều chắc chắn, thì khi xây bức tường (Integration), khả năng sụp đổ sẽ thấp hơn. Nếu không Unit Test, việc debug lỗi khi hệ thống chạy thật sẽ như "mò kim đáy bể".

---

## 4. Các khái niệm liên quan (nên biết để chém gió)

### Mocking (Giả lập)

Unit Test yêu cầu sự **Độc lập (Isolation)**.

- Vấn đề: Hàm Service A gọi hàm Database B. Nếu test Service A mà phải chạy cả Database B thì đó là Integration Test, không phải Unit Test.
- Giải pháp: Dùng **Mock** (như Mockito). Ta tạo ra một Database B "giả". Nó không lưu dữ liệu thật, mà chỉ trả về kết quả giả định. Giúp test Service A chạy cực nhanh và không phụ thuộc mạng hay database.

### JUnit & Mockito (Trong dự án này)

- **JUnit**: Là Framework để chạy test (Chứa `@Test`, `assertEquals`).
- **Mockito**: Là thư viện để tạo Mock object (Chứa `when(...)`, `thenReturn(...)`).

---

## 5. Câu trả lời mẫu khi Giáo viên hỏi

**Giáo viên:** _"Tại sao em phải viết Unit Test? Tại sao không chạy Postman test cho nhanh?"_

**Trả lời:**
"Dạ thưa thầy,

1.  Unit Test giúp em **kiểm tra kỹ từng ngóc ngách logic** (các trường hợp if/else, null, exception) mà chạy Postman rất khó giả lập hết được.
2.  Nó giúp em **phát hiện lỗi ngay lập tức** khi code, không cần đợi build cả ứng dụng lên mới test.
3.  Unit Test chạy **độc lập và rất nhanh** (do dùng Mock), không phụ thuộc vào Database hay mạng, nên có thể chạy tự động liên tục trong quy trình CI/CD ạ."
