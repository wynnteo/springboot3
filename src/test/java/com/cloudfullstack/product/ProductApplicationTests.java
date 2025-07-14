package com.cloudfullstack.product;

import org.junit.jupiter.api.Test;
import com.cloudfullstack.product.dto.CreateProductRequest;
import com.cloudfullstack.product.dto.ProductResponse;
import com.cloudfullstack.product.entity.Product;
import com.cloudfullstack.product.exception.ProductNotFoundException;
import com.cloudfullstack.product.mapper.ProductMapper;
import com.cloudfullstack.product.repository.ProductRepository;
import com.cloudfullstack.product.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductMapper productMapper;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    private CreateProductRequest createRequest;
    private ProductResponse productResponse;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setProductUuid("test-uuid");
        testProduct.setTitle("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(99.99));
        testProduct.setStock(100);
        
        createRequest = new CreateProductRequest();
        createRequest.setTitle("Test Product");
        createRequest.setPrice(BigDecimal.valueOf(99.99));
        createRequest.setStock(100);
        
        productResponse = new ProductResponse();
        productResponse.setProductUuid("test-uuid");
        productResponse.setTitle("Test Product");
        productResponse.setPrice(BigDecimal.valueOf(99.99));
        productResponse.setStock(100);
    }
    
    @Test
    void createProduct_ShouldReturnProductResponse() {
        // Given
        when(productMapper.toEntity(any(CreateProductRequest.class))).thenReturn(testProduct);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);
        
        // When
        ProductResponse result = productService.createProduct(createRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Product", result.getTitle());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toEntity(any(CreateProductRequest.class));
        verify(productMapper).toResponse(any(Product.class));
    }
    
    @Test
    void getProductByUuid_WhenProductExists_ShouldReturnProduct() {
        // Given
        when(productRepository.findByProductUuid(anyString())).thenReturn(Optional.of(testProduct));
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);
        
        // When
        ProductResponse result = productService.getProductByUuid("test-uuid");
        
        // Then
        assertNotNull(result);
        assertEquals("test-uuid", result.getProductUuid());
        
        verify(productRepository).findByProductUuid("test-uuid");
        verify(productMapper).toResponse(testProduct);
    }
    
    @Test
    void getProductByUuid_WhenProductNotExists_ShouldThrowException() {
        // Given
        when(productRepository.findByProductUuid(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductByUuid("non-existent-uuid");
        });
        
        verify(productRepository).findByProductUuid("non-existent-uuid");
        verify(productMapper, never()).toResponse(any(Product.class));
    }
}