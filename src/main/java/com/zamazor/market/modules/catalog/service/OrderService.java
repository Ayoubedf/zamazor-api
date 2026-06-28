package com.zamazor.market.modules.catalog.service;

import com.zamazor.market.modules.catalog.models.dto.AddressRequest;
import com.zamazor.market.modules.catalog.models.dto.StockRestoreDto;
import com.zamazor.market.modules.catalog.models.entity.AddressComponent;
import com.zamazor.market.modules.product.repository.ProductRepository;
import com.zamazor.market.shared.api.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class OrderService {
	private final CartRepository cartRepository;
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final AddressService addressService;
	private final ProductRepository productRepository;
	private final Clock clock;

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
	public OrderDto checkout(User user, CheckoutRequest request) {
		var cart = cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new CartNotFoundException("Cart empty or missing"));

		if (cart.getItems().isEmpty()) {
			throw new EmptyCartException("Cannot checkout an empty cart");
		}

		var order = Order.createFromCart(cart);
		var snapshot = new AddressComponent(
				request.country(),
				request.city(),
				request.street(),
				request.phone()
		);
		order.setShippingAddressSnapshot(snapshot);

		if (request.isDefault()) {
			var addressRequest = new AddressRequest(request.country(), request.city(), request.street(), request.phone());
			addressService.createOrUpdate(user, addressRequest);
		}

		var savedOrder = orderRepository.saveAndFlush(order);
		cart.clear();
		cartRepository.saveAndFlush(cart);

		return orderMapper.toDto(savedOrder);
	}

	@Transactional
	public OrderDto changeStatus(UUID orderId, OrderStatus newStatus) {
		var order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found"));
		LocalDateTime now = LocalDateTime.now(clock);

		if (order.getStatus() == newStatus) return orderMapper.toDto(order);

		switch (newStatus) {
			case CANCELED -> handleCancellation(order, now);
			case REFUNDED -> handleRefund(order, now);
			default -> order.transitionTo(newStatus);
		}

		return orderMapper.toDto(orderRepository.save(order));
	}

	@Transactional
	public OrderDto cancelOrder(UUID orderId, User user) {
		var order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found"));

		if (!user.getIsAdmin() && !order.getUser().getId().equals(user.getId())) {
			throw new UnauthorizedOrderException("You do not have permission to cancel this order");
		}

		if (order.getStatus() == OrderStatus.CANCELED) return orderMapper.toDto(order);

		LocalDateTime now = LocalDateTime.now(clock);
		handleCancellation(order, now);

		return orderMapper.toDto(order);
	}

	private void handleCancellation(Order order, LocalDateTime now) {
		List<StockRestoreDto> itemsToRestore = order.cancel(now);
		if (!itemsToRestore.isEmpty()) {
			restoreInventoryStock(itemsToRestore);
		}
	}

	private void handleRefund(Order order, LocalDateTime now) {
		if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.PAID) {
			throw new IllegalStateException("Only paid or delivered orders can be refunded.");
		}
		List<StockRestoreDto> itemsToRestore = order.refund(now);
		if (!itemsToRestore.isEmpty()) {
			restoreInventoryStock(itemsToRestore);
		}
	}

	private void restoreInventoryStock(List<StockRestoreDto> itemsToRestore) {
		for (StockRestoreDto item : itemsToRestore) {
			productRepository.findById(item.productId())
					.ifPresent(product -> {
						product.restoreStock(item.quantity());
						productRepository.save(product);
					});
		}
	}
}