package com.cloudfullstack.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.mapstruct.Builder;

// DTO for creating new products
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product creation request")
public class CreateProductRequest {

    @NotBlank(message = "Product title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    @Schema(description = "Product title", example = "iPhone 15 Pro")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Product description", example = "Latest iPhone with advanced features")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Price is too high")
    @Schema(description = "Product price", example = "999.99")
    private BigDecimal price;

    @NotBlank(message = "Store ID is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Store ID must contain only uppercase letters, numbers, and hyphens")
    @Schema(description = "Store identifier", example = "STORE-001")
    private String storeId;

    @NotBlank(message = "Category is required")
    @Schema(description = "Product category", example = "Electronics")
    private String category;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Max(value = 10000, message = "Stock quantity is too high")
    @Schema(description = "Available stock", example = "50")
    private Integer stock;
}