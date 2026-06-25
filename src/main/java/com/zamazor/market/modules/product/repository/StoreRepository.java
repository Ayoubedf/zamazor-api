package com.zamazor.market.modules.product.repository;

import com.zamazor.market.modules.product.models.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
	@Query(value = "SELECT * FROM stores LIMIT 1", nativeQuery = true)
	Optional<Store> findOne();
}
