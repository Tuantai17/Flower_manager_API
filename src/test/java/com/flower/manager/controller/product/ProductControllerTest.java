package com.flower.manager.controller.product;

import com.flower.manager.dto.product.ProductDTO;
import com.flower.manager.service.product.ProductService;
import com.flower.manager.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable Security filters for this test
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Red Rose")
                .slug("red-rose")
                .price(new BigDecimal("100.0"))
                .categoryId(1L)
                .build();
    }

    @Test
    void getAll_ShouldReturnProductsWithLinks() throws Exception {
        // Given
        when(productService.getAllActive()).thenReturn(List.of(productDTO));

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(productDTO.getId()))
                .andExpect(jsonPath("$.data[0].links").exists());
    }

    @Test
    void getById_ShouldReturnProductWithLinks() throws Exception {
        // Given
        when(productService.getById(1L)).thenReturn(productDTO);

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.links").exists());
    }
}
