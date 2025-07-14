package com.cloudfullstack.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Error response")
public class ErrorResponse {

    @Schema(description = "Error code", example = "PRODUCT_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Error message", example = "Product not found with UUID: 123")
    private String message;

    @Schema(description = "Error details")
    private List<String> details;

    @Schema(description = "Timestamp when error occurred")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Request path where error occurred", example = "/api/products/123")
    private String path;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String errorCode, String message, String path) {
        this();
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(String errorCode, String message, List<String> details, String path) {
        this();
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
        this.path = path;
    }

}