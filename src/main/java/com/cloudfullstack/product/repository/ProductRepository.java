package com.cloudfullstack.product.repository;

import com.cloudfullstack.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find by UUID instead of ID for external APIs
    Optional<Product> findByProductUuid(String productUuid);

    // Find active products by store
    List<Product> findByStoreIdAndActiveTrue(String storeId);

    // Find products by category with pagination
    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);

    // Search products by title (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) AND p.active = true")
    List<Product> searchByTitle(@Param("title") String title);

    // Find products in price range
    List<Product> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    // Find low stock products
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold AND p.active = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    // Count products by store
    long countByStoreIdAndActiveTrue(String storeId);

    // Update stock quantity
    @Modifying
    @Query("UPDATE Product p SET p.stock = :stock WHERE p.productUuid = :uuid")
    int updateStock(@Param("uuid") String uuid, @Param("stock") Integer stock);

    // Soft delete (mark as inactive)
    @Modifying
    @Query("UPDATE Product p SET p.active = false WHERE p.productUuid = :uuid")
    int deactivateProduct(@Param("uuid") String uuid);

    // Check if product exists and is active
    boolean existsByProductUuidAndActiveTrue(String productUuid);
}