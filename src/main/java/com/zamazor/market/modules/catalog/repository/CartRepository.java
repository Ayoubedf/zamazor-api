package com.zamazor.market.modules.catalog.repository;

import com.zamazor.market.modules.catalog.models.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
	@EntityGraph(attributePaths = {"items", "items.product", "items.product.category"})
	Optional<Cart> findByUserId(UUID userId);
}
