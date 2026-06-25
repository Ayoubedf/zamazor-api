package com.zamazor.market.modules.product.repository;

import com.zamazor.market.modules.product.models.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
	@Query(
			value = "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.store WHERE p.category.id = :categoryId",
			countQuery = "SELECT count(p) FROM Product p WHERE p.category.id = :categoryId"
	)
	Page<Product> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);
}