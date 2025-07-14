package com.cloudfullstack.product.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cloudfullstack.product.dto.CreateProductRequest;
import com.cloudfullstack.product.dto.ProductResponse;
import com.cloudfullstack.product.dto.UpdateProductRequest;
import com.cloudfullstack.product.entity.Product;
import com.cloudfullstack.product.exception.InsufficientStockException;
import com.cloudfullstack.product.exception.ProductNotFoundException;
import com.cloudfullstack.product.mapper.ProductMapper;
import com.cloudfullstack.product.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    // Create new product
    public ProductResponse createProduct(CreateProductRequest request) {
        logger.info("Creating new product with title: {}", request.getTitle());

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        logger.info("Product created successfully with UUID: {}", savedProduct.getProductUuid());
        return productMapper.toResponse(savedProduct);
    }

    // Get all products with pagination
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size, String sortBy, String sortDirection) {
        logger.info("Fetching products - page: {}, size: {}, sort: {} {}", page, size, sortBy, sortDirection);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toResponse);
    }

    // Get product by UUID
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#uuid")
    public ProductResponse getProductByUuid(String uuid) {
        logger.info("Fetching product with UUID: {}", uuid);

        Product product = productRepository.findByProductUuid(uuid)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with UUID: " + uuid));

        return productMapper.toResponse(product);
    }

    // Get products by store
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByStore(String storeId) {
        logger.info("Fetching products for store: {}", storeId);

        List<Product> products = productRepository.findByStoreIdAndActiveTrue(storeId);
        return productMapper.toResponseList(products);
    }

    // Search products by title
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String title) {
        logger.info("Searching products with title containing: {}", title);

        List<Product> products = productRepository.searchByTitle(title);
        return productMapper.toResponseList(products);
    }

    // Get products by price range
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.info("Fetching products with price between {} and {}", minPrice, maxPrice);

        List<Product> products = productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice);
        return productMapper.toResponseList(products);
    }

    // Update product
    @CacheEvict(value = "products", key = "#uuid")
    public ProductResponse updateProduct(String uuid, UpdateProductRequest request) {
        logger.info("Updating product with UUID: {}", uuid);

        Product product = productRepository.findByProductUuid(uuid)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with UUID: " + uuid));

        productMapper.updateEntityFromRequest(request, product);
        Product updatedProduct = productRepository.save(product);

        logger.info("Product updated successfully with UUID: {}", uuid);
        return productMapper.toResponse(updatedProduct);
    }

    // Update stock quantity
    @CacheEvict(value = "products", key = "#uuid")
    public void updateStock(String uuid, Integer quantity) {
        logger.info("Updating stock for product UUID: {} to quantity: {}", uuid, quantity);

        if (!productRepository.existsByProductUuidAndActiveTrue(uuid)) {
            throw new ProductNotFoundException("Product not found with UUID: " + uuid);
        }

        int updatedRows = productRepository.updateStock(uuid, quantity);
        if (updatedRows == 0) {
            throw new ProductNotFoundException("Failed to update stock for product UUID: " + uuid);
        }

        logger.info("Stock updated successfully for product UUID: {}", uuid);
    }

    // Reduce stock (for purchases)
    @CacheEvict(value = "products", key = "#uuid")
    public void reduceStock(String uuid, Integer quantity) {
        logger.info("Reducing stock for product UUID: {} by quantity: {}", uuid, quantity);

        Product product = productRepository.findByProductUuid(uuid)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with UUID: " + uuid));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                "Insufficient stock. Available: " + product.getStock() + ", Requested: " + quantity);
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        logger.info("Stock reduced successfully for product UUID: {}", uuid);
    }

    // Soft delete product
    @CacheEvict(value = "products", key = "#uuid")
    public void deleteProduct(String uuid) {
        logger.info("Deleting product with UUID: {}", uuid);

        int updatedRows = productRepository.deactivateProduct(uuid);
        if (updatedRows == 0) {
            throw new ProductNotFoundException("Product not found with UUID: " + uuid);
        }

        logger.info("Product deleted successfully with UUID: {}", uuid);
    }

    // Get low stock products
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(Integer threshold) {
        logger.info("Fetching products with stock below: {}", threshold);

        List<Product> products = productRepository.findLowStockProducts(threshold);
        return productMapper.toResponseList(products);
    }

    // Get product count by store
    @Transactional(readOnly = true)
    public long getProductCountByStore(String storeId) {
        logger.info("Counting products for store: {}", storeId);

        return productRepository.countByStoreIdAndActiveTrue(storeId);
    }
}
