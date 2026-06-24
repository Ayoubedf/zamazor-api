package com.zamazor.market.modules.catalog.repository;

import com.zamazor.market.modules.catalog.models.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
	Page<Order> findByUserId(UUID userId, Pageable pageable);
}
