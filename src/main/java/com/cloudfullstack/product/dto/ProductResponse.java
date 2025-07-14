package com.cloudfullstack.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product response")
public class ProductResponse {

    @Schema(description = "Product UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String productUuid;

    @Schema(description = "Product title", example = "iPhone 15 Pro")
    private String title;

    @Schema(description = "Product description")
    private String description;

    @Schema(description = "Product price", example = "999.99")
    private BigDecimal price;

    @Schema(description = "Store identifier", example = "STORE-001")
    private String storeId;

    @Schema(description = "Product category", example = "Electronics")
    private String category;

    @Schema(description = "Available stock", example = "50")
    private Integer stock;

    @Schema(description = "Product status", example = "true")
    private Boolean active;

    @JsonProperty("created_at")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}