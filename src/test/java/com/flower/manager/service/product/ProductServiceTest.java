package com.flower.manager.service.product;

import com.flower.manager.dto.product.ProductCreateDTO;
import com.flower.manager.dto.product.ProductDTO;
import com.flower.manager.entity.Category;
import com.flower.manager.entity.Product;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.mapper.ProductMapper;
import com.flower.manager.repository.CategoryRepository;
import com.flower.manager.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;
    private ProductCreateDTO createDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Rose");

        product = Product.builder()
                .id(1L)
                .name("Red Rose")
                .slug("red-rose")
                .price(new BigDecimal("100.0"))
                .category(category)
                .build();

        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Red Rose")
                .slug("red-rose")
                .price(new BigDecimal("100.0"))
                .categoryId(1L)
                .build();

        createDTO = ProductCreateDTO.builder()
                .name("Red Rose")
                .slug("red-rose")
                .price(new BigDecimal("100.0"))
                .categoryId(1L)
                .build();
    }

    @Test
    void create_ShouldReturnProductDTO_WhenSuccessful() {
        // Given
        when(productRepository.existsBySlug(createDTO.getSlug())).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(createDTO)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // When
        ProductDTO result = productService.create(createDTO);

        // Then
        assertNotNull(result);
        assertEquals(productDTO.getName(), result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void create_ShouldThrowException_WhenSlugExists() {
        // Given
        when(productRepository.existsBySlug(createDTO.getSlug())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> productService.create(createDTO));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getById_ShouldReturnProductDTO_WhenIdExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // When
        ProductDTO result = productService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_ShouldThrowException_WhenIdDoesNotExist() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getById(1L));
    }
}
