package com.flower.manager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Cấu hình OpenAPI (Swagger) cho Flower Manager API
 * - Truy cập Swagger UI: http://localhost:8080/swagger-ui/index.html
 * - Truy cập OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Thông tin API
                .info(new Info()
                        .title("🌸 Flower Manager API")
                        .version("1.0.0")
                        .description(
                                """
                                        API Documentation cho ứng dụng quản lý cửa hàng hoa.

                                        ## Tính năng chính:
                                        - **Authentication**: Đăng ký, đăng nhập, quản lý JWT token
                                        - **Products**: Quản lý sản phẩm hoa
                                        - **Categories**: Quản lý danh mục
                                        - **Orders**: Quản lý đơn hàng
                                        - **Cart**: Giỏ hàng
                                        - **Reviews**: Đánh giá sản phẩm
                                        - **Vouchers**: Mã giảm giá
                                        - **Live Chat**: Chat hỗ trợ với AI/Staff

                                        ## Authentication:
                                        Sử dụng JWT Bearer Token. Thêm header `Authorization: Bearer <token>` cho các API cần xác thực.
                                        """)
                        .contact(new Contact()
                                .name("Flower Manager Team")
                                .email("support@flowermanager.com")
                                .url("https://flowermanager.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // Server URLs
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.flowermanager.com")
                                .description("Production Server")))
                // Security scheme (JWT Bearer)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "Nhập JWT token để xác thực. Ví dụ: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")));
    }
}
