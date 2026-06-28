package com.zamazor.market.modules.product.repository;

import com.zamazor.market.modules.product.models.entity.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
	@EntityGraph(attributePaths = {"category"})
	@Override
	@NonNull Optional<Product> findById(@NonNull UUID id);

	@EntityGraph(attributePaths = {"category"})
	@Override
	@NonNull Page<Product> findAll(@NonNull Pageable pageable);

	@EntityGraph(attributePaths = {"category"})
	@NonNull
	@Override
	Page<Product> findAll(@NonNull Specification<Product> specification, @NonNull Pageable pageable);

	@EntityGraph(attributePaths = {"category"})
	Page<Product> findByCategoryId(UUID categoryId, Pageable pageable);
}