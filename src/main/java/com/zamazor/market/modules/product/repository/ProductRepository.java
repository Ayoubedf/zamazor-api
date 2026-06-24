package com.zamazor.market.modules.product.repository;

import com.zamazor.market.modules.product.models.entity.Category;
import com.zamazor.market.modules.product.models.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
   @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.store")
    List<Product> findAllWithAssociations();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.store WHERE p.category = :category")
    List<Product> findByCategory(@Param("category") Category category);
}