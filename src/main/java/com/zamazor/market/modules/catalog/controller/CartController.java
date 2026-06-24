package com.zamazor.market.modules.catalog.controller;

import com.zamazor.market.modules.catalog.models.dto.AddToCartRequest;
import com.zamazor.market.modules.catalog.models.dto.CartDto;
import com.zamazor.market.modules.catalog.models.dto.UpdateQuantityRequest;
import com.zamazor.market.modules.catalog.service.CartService;
import com.zamazor.market.modules.user.models.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
	private final CartService cartService;

	@GetMapping
	public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(cartService.getCartByUserId(user.getId()));
	}

	@PostMapping("/items")
	public ResponseEntity<CartDto> addItemToCart(@AuthenticationPrincipal User user, @Valid @RequestBody AddToCartRequest request) {
		return ResponseEntity.ok(cartService.addItemToCart(user.getId(), request));
	}

	@PutMapping("/items/{productId}")
	public ResponseEntity<CartDto> updateItemQuantity(
			@AuthenticationPrincipal User user,
			@PathVariable UUID productId,
			@Valid @RequestBody UpdateQuantityRequest request
	) {
		return ResponseEntity.ok(cartService.updateItemQuantity(user.getId(), productId, request.quantity()));
	}

	@DeleteMapping("/items/{itemId}")
	public ResponseEntity<CartDto> removeItemFromCart(@AuthenticationPrincipal User user, @PathVariable UUID itemId) {
		return ResponseEntity.ok(cartService.removeItem(user.getId(), itemId));
	}

	@DeleteMapping
	public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
		cartService.clearCart(user.getId());
		return ResponseEntity.noContent().build();
	}
}
