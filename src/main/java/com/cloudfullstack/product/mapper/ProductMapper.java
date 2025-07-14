package com.cloudfullstack.product.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloudfullstack.product.dto.CreateProductRequest;
import com.cloudfullstack.product.dto.ProductResponse;
import com.cloudfullstack.product.dto.UpdateProductRequest;
import com.cloudfullstack.product.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Convert entity to response DTO
    ProductResponse toResponse(Product product);

    // Convert list of entities to list of response DTOs
    List<ProductResponse> toResponseList(List<Product> products);

    // Convert create request to entity
    Product toEntity(CreateProductRequest request);

    // Update entity from update request (ignore null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateProductRequest request, @MappingTarget Product product);
}