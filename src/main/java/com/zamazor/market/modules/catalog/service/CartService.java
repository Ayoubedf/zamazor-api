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
import com.zamazor.market.modules.user.repository.UserRepository;
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
	private final UserRepository userRepository;

	public CartDto getCartByUserId(UUID userId) {
		return cartRepository.findByUserId(userId)
				.map(cartMapper::toDto)
				.orElseThrow(CartNotFoundException::new);
	}

	@Transactional
	public CartDto addItemToCart(User user, AddToCartRequest request) {
		var cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
			var newCart = new Cart();
			newCart = cartRepository.saveAndFlush(newCart);
			user.setCart(newCart);
			newCart.setUser(user);

			userRepository.saveAndFlush(user);
			return newCart;
		});

		var product = productRepository.findById(request.productId())
				.orElseThrow(() -> new ProductNotFoundException(request.productId()));

		cart.addProduct(product, request.quantity());
		return cartMapper.toDto(cartRepository.saveAndFlush(cart));
	}

	@Transactional
	public CartDto updateItemQuantity(UUID userId, UUID productId, Integer quantity) {
		var cart = cartRepository.findByUserId(userId)
				.orElseThrow(CartNotFoundException::new);
		var product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));

		cart.updateItemQuantity(product, quantity);

		return cartMapper.toDto(cartRepository.save(cart));
	}

	@Transactional
	public CartDto removeItem(UUID userId, UUID productId) {
		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(CartNotFoundException::new);

		cart.removeItem(productId);

		return cartMapper.toDto(cartRepository.save(cart));
	}

	@Transactional
	public void clearCart(UUID userId) {
		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(CartNotFoundException::new);

		cart.clear();
	}
}