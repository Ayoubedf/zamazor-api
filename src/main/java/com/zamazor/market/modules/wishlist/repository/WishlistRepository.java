package com.zamazor.market.modules.wishlist.repository;

import com.zamazor.market.modules.wishlist.models.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
	Page<Wishlist> findByUserId(UUID userId, Pageable pageable);

	boolean existsByUserIdAndProductId(UUID userId, UUID productId);

	Optional<Wishlist> findByUserIdAndProductId(UUID userId, UUID productId);

	void deleteByUserId(UUID userId);

	void deleteByUserIdAndProductId(UUID userId, UUID productId);
}
