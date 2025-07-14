package com.cloudfullstack.product.controller;

import com.cloudfullstack.product.dto.CreateProductRequest;
import com.cloudfullstack.product.dto.ProductResponse;
import com.cloudfullstack.product.dto.UpdateProductRequest;
import com.cloudfullstack.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs for managing products")
@Validated
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all products with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
        @Parameter(description = "Page number (0-based)")
        @RequestParam(defaultValue = "0") @Min(0) int page,

        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "10") @Min(1) int size,

        @Parameter(description = "Sort field")
        @RequestParam(defaultValue = "createdAt") String sortBy,

        @Parameter(description = "Sort direction")
        @RequestParam(defaultValue = "desc") String sortDirection) {

        Page<ProductResponse> products = productService.getAllProducts(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get product by UUID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProduct(
        @Parameter(description = "Product UUID")
        @PathVariable String uuid) {

        ProductResponse product = productService.getProductByUuid(uuid);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ProductResponse> updateProduct(
        @Parameter(description = "Product UUID")
        @PathVariable String uuid,
        @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse response = productService.updateProduct(uuid, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete product (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "Product UUID")
        @PathVariable String uuid) {

        productService.deleteProduct(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get products by store")
    public ResponseEntity<List<ProductResponse>> getProductsByStore(
        @Parameter(description = "Store ID")
        @PathVariable String storeId) {

        List<ProductResponse> products = productService.getProductsByStore(storeId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by title")
    public ResponseEntity<List<ProductResponse>> searchProducts(
        @Parameter(description = "Search term")
        @RequestParam String title) {

        List<ProductResponse> products = productService.searchProducts(title);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{uuid}/stock")
    @Operation(summary = "Update product stock")
    public ResponseEntity<Void> updateStock(
        @Parameter(description = "Product UUID")
        @PathVariable String uuid,
        @Parameter(description = "New stock quantity")
        @RequestParam @Min(0) Integer quantity) {

        productService.updateStock(uuid, quantity);
        return ResponseEntity.ok().build();
    }
}