package com.zamazor.market.modules.wishlist.service;

import com.zamazor.market.modules.product.repository.ProductRepository;
import com.zamazor.market.modules.user.models.entity.User;
import com.zamazor.market.modules.wishlist.models.dto.WishlistDto;
import com.zamazor.market.modules.wishlist.models.entity.Wishlist;
import com.zamazor.market.modules.wishlist.models.mapper.WishlistMapper;
import com.zamazor.market.modules.wishlist.repository.WishlistRepository;
import com.zamazor.market.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {
	private final WishlistRepository wishlistRepository;
	private final ProductRepository productRepository;
	private final WishlistMapper wishlistMapper;

	public PageResponse<WishlistDto> getUserWishlist(UUID userId, Pageable pageable) {
		Page<WishlistDto> wishlistPage = wishlistRepository.findByUserId(userId, pageable)
				.map(wishlistMapper::toDto);
		return new PageResponse<>(wishlistPage);
	}

	@Transactional
	public WishlistDto addToWishlist(User user, UUID productId) {
		if (wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
			throw new IllegalStateException("Product is already in the wishlist");
		}

		var product = productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found"));

		var wishlist = wishlistMapper.toEntity(user, product);

		Wishlist saved = wishlistRepository.save(wishlist);
		return wishlistMapper.toDto(saved);
	}

	@Transactional
	public void removeFromWishlist(UUID userId, UUID productId) {
		Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
				.orElseThrow(() -> new EntityNotFoundException("Item not found in wishlist"));
		wishlistRepository.delete(wishlist);
	}
}