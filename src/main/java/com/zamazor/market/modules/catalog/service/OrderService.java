package com.zamazor.market.modules.catalog.service;

import com.zamazor.market.modules.catalog.exception.*;
import com.zamazor.market.modules.catalog.models.dto.CheckoutRequest;
import com.zamazor.market.modules.catalog.models.dto.OrderDto;
import com.zamazor.market.modules.catalog.models.entity.Order;
import com.zamazor.market.modules.catalog.models.mapper.OrderMapper;
import com.zamazor.market.modules.catalog.repository.CartRepository;
import com.zamazor.market.modules.catalog.repository.OrderRepository;
import com.zamazor.market.modules.user.models.entity.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class OrderService {
	private final CartRepository cartRepository;
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;

	public List<OrderDto> getAll() {
		return orderRepository.findAll().stream().map(orderMapper::toDto).toList();
	}

	public List<OrderDto> getByUserId(UUID userId) {
		var orders = orderRepository.findByUserId(userId);
		return orders.stream().map(orderMapper::toDto).toList();
	}

	public OrderDto getById(UUID id) {
		var order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException("Order with id: " + id + " was not found"));
		return orderMapper.toDto(order);
	}

	@Transactional
	public OrderDto checkout(UUID userId, CheckoutRequest request) {
		var cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart empty or missing"));

		if (cart.getItems().isEmpty()) {
			throw new EmptyCartException("Cannot checkout an empty cart");
		}

		var order = Order.createFromCart(cart);
		var savedOrder = orderRepository.save(order);

		cart.clear();

		return orderMapper.toDto(savedOrder);
	}

	@Transactional
	public OrderDto cancelOrder(UUID orderId, @NonNull User user) {
		var order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found"));

		if (!user.getIsAdmin() && !order.getUser().getId().equals(user.getId())) {
			throw new UnauthorizedOrderException("You do not have permission to cancel this order");
		}

		order.cancel();

		return orderMapper.toDto(order);
	}
}