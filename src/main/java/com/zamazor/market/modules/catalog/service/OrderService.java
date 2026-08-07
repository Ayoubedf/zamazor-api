package com.zamazor.market.modules.catalog.service;

import com.stripe.model.checkout.Session;
import com.zamazor.market.mail.event.OrderPlacedEvent;
import com.zamazor.market.mail.event.OrderStatusChangedEvent;
import com.zamazor.market.modules.catalog.models.dto.*;
import com.zamazor.market.modules.catalog.models.entity.AddressComponent;
import com.zamazor.market.modules.catalog.models.mapper.PaymentMapper;
import com.zamazor.market.modules.product.repository.ProductRepository;
import com.zamazor.market.modules.user.exception.UserNotFoundException;
import com.zamazor.market.modules.user.repository.UserRepository;
import com.zamazor.market.payment.exception.PaymentGatewayException;
import com.zamazor.market.payment.service.PaymentService;
import com.zamazor.market.shared.api.PageResponse;
import com.zamazor.market.modules.catalog.exception.*;
import com.zamazor.market.modules.catalog.models.entity.Order;
import com.zamazor.market.modules.catalog.models.entity.OrderStatus;
import com.zamazor.market.modules.catalog.models.mapper.OrderMapper;
import com.zamazor.market.modules.catalog.repository.CartRepository;
import com.zamazor.market.modules.catalog.repository.OrderRepository;
import com.zamazor.market.modules.catalog.specification.OrderSpecifications;
import com.zamazor.market.modules.user.models.entity.User;
import com.zamazor.market.shared.model.dto.PricingInfo;
import com.zamazor.market.shared.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
	private final ApplicationEventPublisher eventPublisher;
	private final UserRepository userRepository;
	private final PaymentService paymentService;
	private final PaymentMapper paymentMapper;
	private final PricingService pricingService;

	public PageResponse<OrderDto> getAll(String userFullName, OrderStatus status, Pageable pageable) {
		Specification<Order> spec = OrderSpecifications.createSpec(userFullName, status);

		Page<Order> orderPage = orderRepository.findAll(spec, pageable);
		return new PageResponse<>(orderPage.map(orderMapper::toDto));
	}

	public PageResponse<OrderDto> getByUserId(UUID userId, Pageable pageable) {
		Page<OrderDto> orderPage = orderRepository.findByUserId(userId, pageable).map(orderMapper::toDto);
		return new PageResponse<>(orderPage);
	}

	public OrderDto getById(UUID id) {
		var order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException(id));
		return orderMapper.toDto(order);
	}

	@Transactional
	public OrderDto checkout(User user, CheckoutRequest request) {
		var cart = cartRepository.findByUserId(user.getId())
				.orElseThrow(CartNotFoundException::new);

		if (cart.getItems().isEmpty()) {
			throw new EmptyCartException("Cannot checkout an empty cart");
		}

		PricingInfo pricing = pricingService.calculate(cart, user);
		var order = Order.createFromCart(cart, pricing);
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

		String paymentUrl = paymentService.createCheckoutSession(savedOrder).getUrl();
		eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder, user, paymentUrl));

		return orderMapper.toDto(savedOrder);
	}

	public PaymentSessionResponse regeneratePaymentLink(UUID orderId) {
		var order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId));

		//  Order is already processed or completed
		if (order.getStatus() != OrderStatus.PENDING) {
			throw new IllegalOrderStateException("Order Already Processed or completed");
		}

		Session session = paymentService.createCheckoutSession(order);
		return paymentMapper.toDto(session);
	}

	@Transactional
	public OrderDto changeStatus(UUID orderId, OrderStatus newStatus) {
		var order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId));
		var user = userRepository.findById(order.getUser().getId())
				.orElseThrow(() -> new UserNotFoundException(order.getUser().getId()));
		LocalDateTime now = LocalDateTime.now(clock);

		if (order.getStatus() == newStatus) return orderMapper.toDto(order);

		switch (newStatus) {
			case CANCELED -> handleCancellation(order, now);
			case REFUNDED -> handleRefund(order, now);
			default -> order.transitionTo(newStatus);
		}

		var savedOrder = orderRepository.save(order);
		eventPublisher.publishEvent(new OrderStatusChangedEvent(savedOrder, user, newStatus));

		return orderMapper.toDto(savedOrder);
	}

	@Transactional
	public OrderDto cancelOrder(UUID orderId, User user) {
		var order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId));

		if (!user.getIsAdmin() && !order.getUser().getId().equals(user.getId())) {
			throw new UnauthorizedOrderException("You do not have permission to cancel this order");
		}

		if (order.getStatus() == OrderStatus.CANCELED) return orderMapper.toDto(order);

		LocalDateTime now = LocalDateTime.now(clock);
		handleCancellation(order, now);

		var savedOrder = orderRepository.save(order);
		eventPublisher.publishEvent(new OrderStatusChangedEvent(savedOrder, user, OrderStatus.CANCELED));

		return orderMapper.toDto(savedOrder);
	}

	@Transactional
	public OrderDto verifyAndCompleteOrder(UUID orderId, String sessionId, User user) {
		var order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId));

		if (!order.getUser().getId().equals(user.getId()))
			throw new UnauthorizedOrderException("You do not have permission to modify this order");
		if (order.getStatus() == OrderStatus.PAID)
			return orderMapper.toDto(order);
		if (order.getStatus() != OrderStatus.PENDING)
			throw new IllegalOrderStateException("Order is not in a payable state");
		if (!paymentService.isSessionPaid(sessionId)) {
			throw new PaymentGatewayException("Payment verification failed. Transaction is incomplete or declined.");
		}

		order.setStatus(OrderStatus.PAID);
		var savedOrder = orderRepository.save(order);

		eventPublisher.publishEvent(new OrderStatusChangedEvent(savedOrder, user, OrderStatus.PAID));

		return orderMapper.toDto(savedOrder);
	}

	private void handleCancellation(Order order, LocalDateTime now) {
		List<StockRestoreDto> itemsToRestore = order.cancel(now);
		if (!itemsToRestore.isEmpty()) {
			restoreInventoryStock(itemsToRestore);
		}
	}

	private void handleRefund(Order order, LocalDateTime now) {
		if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.PAID) {
			throw new IllegalOrderStateException("Only paid or delivered orders can be refunded.");
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