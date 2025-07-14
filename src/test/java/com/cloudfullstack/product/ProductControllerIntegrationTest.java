package com.cloudfullstack.product;

import com.cloudfullstack.product.controller.ProductController;
import com.cloudfullstack.product.dto.CreateProductRequest;
import com.cloudfullstack.product.dto.UpdateProductRequest;
import com.cloudfullstack.product.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.any;

import java.math.BigDecimal;
import java.util.Arrays;

import com.cloudfullstack.product.service.ProductService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateProductRequest createProductRequest;
    private UpdateProductRequest updateProductRequest;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        createProductRequest = new CreateProductRequest(
            "iPhone 15 Pro",
            "Latest iPhone with advanced features",
            new BigDecimal("999.99"),
            "STORE-001",
            "Electronics",
            50
        );

        updateProductRequest = new UpdateProductRequest(
            "iPhone 15 Pro Updated",
            "Updated description",
            new BigDecimal("899.99"),
            25
        );

        testProduct = new Product(
            "iPhone 15 Pro",
            "Latest iPhone with advanced features",
            new BigDecimal("999.99"),
            "STORE-001",
            "Electronics",
            50
        );
        testProduct.setProductUuid("550e8400-e29b-41d4-a716-446655440000");
        testProduct.setActive(true);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createProduct_Success() throws Exception {
        when(productService.createProduct(any(CreateProductRequest.class)))
            .thenReturn(mapToProductResponse(testProduct));
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productUuid").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.title").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.description").value("Latest iPhone with advanced features"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.storeId").value("STORE-001"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(productService).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void createProduct_ValidationError_BlankTitle() throws Exception {
        createProductRequest.setTitle("");
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(org.mockito.Mockito.any(CreateProductRequest.class));
    }

    @Test
    void createProduct_ValidationError_InvalidPrice() throws Exception {
        createProductRequest.setPrice(new BigDecimal("0"));

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void createProduct_ValidationError_NegativeStock() throws Exception {
        createProductRequest.setStock(-1);
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void getAllProducts_Success() throws Exception {
        List<com.cloudfullstack.product.dto.ProductResponse> products = Arrays.asList(
            mapToProductResponse(testProduct)
        );
        Page<com.cloudfullstack.product.dto.ProductResponse> page = 
            new PageImpl<>(products, PageRequest.of(0, 10), 1);

        when(productService.getAllProducts(0, 10, "createdAt", "desc"))
            .thenReturn(page);

        mockMvc.perform(get("/api/v1/products")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "createdAt")
                .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].productUuid").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.content[0].title").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(productService).getAllProducts(0, 10, "createdAt", "desc");
    }

    @Test
    void getProduct_Success() throws Exception {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        when(productService.getProductByUuid(uuid))
            .thenReturn(mapToProductResponse(testProduct));
        mockMvc.perform(get("/api/v1/products/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productUuid").value(uuid))
                .andExpect(jsonPath("$.title").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.storeId").value("STORE-001"));

        verify(productService).getProductByUuid(uuid);
    }

    @Test
    void updateProduct_Success() throws Exception {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        Product updatedProduct = new Product(
            "iPhone 15 Pro Updated",
            "Updated description",
            new BigDecimal("899.99"),
            "STORE-001",
            "Electronics",
            25
        );
        updatedProduct.setProductUuid(uuid);
        updatedProduct.setActive(true);
        updatedProduct.setCreatedAt(LocalDateTime.now());
        updatedProduct.setUpdatedAt(LocalDateTime.now());

        when(productService.updateProduct(eq(uuid), any(UpdateProductRequest.class)))
            .thenReturn(mapToProductResponse(updatedProduct));
        mockMvc.perform(put("/api/v1/products/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProductRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productUuid").value(uuid))
                .andExpect(jsonPath("$.title").value("iPhone 15 Pro Updated"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value(899.99))
                .andExpect(jsonPath("$.stock").value(25));

        verify(productService).updateProduct(eq(uuid), any(UpdateProductRequest.class));
    }

    @Test
    void updateProduct_ValidationError_InvalidPrice() throws Exception {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        updateProductRequest.setPrice(new BigDecimal("0"));
        mockMvc.perform(put("/api/v1/products/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProductRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateProduct(anyString(), any(UpdateProductRequest.class));
    }

    @Test
    void deleteProduct_Success() throws Exception {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        doNothing().when(productService).deleteProduct(uuid);
        mockMvc.perform(delete("/api/v1/products/{uuid}", uuid))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(uuid);
    }

    @Test
    void getProductsByStore_Success() throws Exception {
        String storeId = "STORE-001";
        List<com.cloudfullstack.product.dto.ProductResponse> products = Arrays.asList(
            mapToProductResponse(testProduct)
        );

        when(productService.getProductsByStore(storeId))
            .thenReturn(products);
        mockMvc.perform(get("/api/v1/products/store/{storeId}", storeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productUuid").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$[0].storeId").value(storeId));

        verify(productService).getProductsByStore(storeId);
    }

    @Test
    void searchProducts_Success() throws Exception {
        String searchTerm = "iPhone";
        List<com.cloudfullstack.product.dto.ProductResponse> products = Arrays.asList(
            mapToProductResponse(testProduct)
        );

        when(productService.searchProducts(searchTerm))
            .thenReturn(products);
        mockMvc.perform(get("/api/v1/products/search")
                .param("title", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(containsString("iPhone")));

        verify(productService).searchProducts(searchTerm);
    }

    @Test
    void updateStock_Success() throws Exception {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        Integer newStock = 100;
        doNothing().when(productService).updateStock(uuid, newStock);

        mockMvc.perform(post("/api/v1/products/{uuid}/stock", uuid)
                .param("quantity", newStock.toString()))
                .andExpect(status().isOk());

        verify(productService).updateStock(uuid, newStock);
    }

    @Test
    void updateStock_ValidationError_NegativeQuantity() throws Exception {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        Integer negativeStock = -1;

        mockMvc.perform(post("/api/v1/products/{uuid}/stock", uuid)
                .param("quantity", negativeStock.toString()))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateStock(anyString(), anyInt());
    }

    private com.cloudfullstack.product.dto.ProductResponse mapToProductResponse(Product product) {
        return new com.cloudfullstack.product.dto.ProductResponse(
            product.getProductUuid(),
            product.getTitle(),
            product.getDescription(),
            product.getPrice(),
            product.getStoreId(),
            product.getCategory(),
            product.getStock(),
            product.getActive(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}