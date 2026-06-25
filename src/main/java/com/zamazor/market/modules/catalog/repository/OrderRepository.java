package com.zamazor.market.modules.catalog.repository;

import com.zamazor.market.modules.catalog.models.entity.Order;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
	@NonNull Page<Order> findAll(@NonNull Specification<Order> specification, @NonNull Pageable pageable);

	Page<Order> findByUserId(UUID userId, Pageable pageable);

	@EntityGraph(attributePaths = {"items"})
	@NonNull Optional<Order> findById(@NonNull UUID id);
}
