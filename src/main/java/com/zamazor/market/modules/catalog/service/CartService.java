package com.zamazor.market.modules.catalog.service;

import com.zamazor.market.modules.catalog.exception.CartNotFoundException;
import com.zamazor.market.modules.catalog.models.dto.AddToCartRequest;
import com.zamazor.market.modules.catalog.models.dto.CartDto;
import com.zamazor.market.modules.catalog.models.entity.Cart;
import com.zamazor.market.modules.catalog.models.mapper.CartMapper;
import com.zamazor.market.modules.catalog.repository.CartRepository;
import com.zamazor.market.modules.product.exception.ProductNotFoundException;
import com.zamazor.market.modules.product.repository.ProductRepository;
import com.zamazor.market.modules.user.models.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final CartMapper cartMapper;

	public CartDto getCartByUserId(UUID userId) {
		var cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
		return cartMapper.toDto(cart);
	}

	@Transactional
	public CartDto addItemToCart(User user, AddToCartRequest request) {
		var cart = cartRepository.findByUserId(user.getId())
				.orElseGet(() -> {
					Cart newCart = new Cart();
					newCart.setUser(user);
					return cartRepository.save(newCart);
				});
		var product = productRepository.findById(request.productId())
				.orElseThrow(() -> new ProductNotFoundException("Product not found"));

		cart.addProduct(product, request.quantity());

		var savedCart = cartRepository.saveAndFlush(cart);

		return cartMapper.toDto(savedCart);
	}

	@Transactional
	public CartDto updateItemQuantity(UUID userId, UUID productId, Integer quantity) {
		var cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
		var product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product with id: " + productId + " not found"));

		cart.updateItemQuantity(product, quantity);

		return cartMapper.toDto(cartRepository.save(cart));
	}

	@Transactional
	public CartDto removeItem(UUID userId, UUID itemId) {
		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

		cart.removeItem(itemId);

		return cartMapper.toDto(cartRepository.save(cart));
	}

	@Transactional
	public void clearCart(UUID userId) {
		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

		cart.clear();
	}
}