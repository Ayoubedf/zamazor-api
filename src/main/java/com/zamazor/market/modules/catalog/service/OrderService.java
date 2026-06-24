package com.zamazor.market.modules.catalog.service;

import com.zamazor.market.common.api.PageResponse;
import com.zamazor.market.modules.catalog.exception.*;
import com.zamazor.market.modules.catalog.models.dto.CheckoutRequest;
import com.zamazor.market.modules.catalog.models.dto.OrderDto;
import com.zamazor.market.modules.catalog.models.entity.Order;
import com.zamazor.market.modules.catalog.models.entity.OrderStatus;
import com.zamazor.market.modules.catalog.models.mapper.OrderMapper;
import com.zamazor.market.modules.catalog.repository.CartRepository;
import com.zamazor.market.modules.catalog.repository.OrderRepository;
import com.zamazor.market.modules.catalog.specification.OrderSpecifications;
import com.zamazor.market.modules.user.models.entity.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class OrderService {
	private final CartRepository cartRepository;
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;

	public PageResponse<OrderDto> getAll(String userFullName, OrderStatus status, Pageable pageable) {
		Specification<Order> spec = OrderSpecifications.createSpec(userFullName, status);
		Page<OrderDto> orderPage = orderRepository.findAll(spec, pageable).map(orderMapper::toDto);
		return new PageResponse<>(orderPage);
	}

	public PageResponse<OrderDto> getByUserId(UUID userId, Pageable pageable) {
		Page<OrderDto> orderPage = orderRepository.findByUserId(userId, pageable).map(orderMapper::toDto);
		return new PageResponse<>(orderPage);
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
		var savedOrder = orderRepository.saveAndFlush(order);

		cart.clear();
		cartRepository.saveAndFlush(cart);

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