package com.cloudfullstack.product.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product update request")
public class UpdateProductRequest {

    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    @Schema(description = "Product title", example = "iPhone 15 Pro Updated")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Product description")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Price is too high")
    @Schema(description = "Product price", example = "899.99")
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    @Max(value = 10000, message = "Stock quantity is too high")
    @Schema(description = "Available stock", example = "25")
    private Integer stock;
}