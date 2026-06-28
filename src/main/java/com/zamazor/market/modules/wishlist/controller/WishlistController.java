package com.zamazor.market.modules.wishlist.controller;

import com.zamazor.market.modules.user.models.entity.User;
import com.zamazor.market.modules.wishlist.models.dto.WishlistDto;
import com.zamazor.market.modules.wishlist.service.WishlistService;
import com.zamazor.market.shared.api.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wishlists")
@RequiredArgsConstructor
public class WishlistController {
	private final WishlistService wishlistService;

	@GetMapping
	public ResponseEntity<PageResponse<WishlistDto>> getWishlist(
			@AuthenticationPrincipal User user,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(wishlistService.getUserWishlist(user.getId(), pageable));
	}

	@PostMapping("/{productId}")
	public ResponseEntity<WishlistDto> toggleToWishlist(
			@AuthenticationPrincipal User user,
			@PathVariable UUID productId) {
		WishlistDto item = wishlistService.toggleToWishlist(user, productId);
		if (item == null) return ResponseEntity.noContent().build();

		return ResponseEntity.status(HttpStatus.CREATED).body(item);
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<Void> removeFromWishlist(
			@AuthenticationPrincipal User user,
			@PathVariable UUID productId) {
		wishlistService.removeFromWishlist(user.getId(), productId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> clearWishlist(@AuthenticationPrincipal User user) {
		wishlistService.clearWishlist(user.getId());
		return ResponseEntity.noContent().build();
	}
}
