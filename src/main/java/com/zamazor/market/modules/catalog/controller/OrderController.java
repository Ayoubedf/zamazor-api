package com.zamazor.market.modules.catalog.controller;

import com.zamazor.market.common.api.PageResponse;
import com.zamazor.market.modules.catalog.models.dto.CheckoutRequest;
import com.zamazor.market.modules.catalog.models.dto.OrderDto;
import com.zamazor.market.modules.catalog.service.OrderService;
import com.zamazor.market.modules.user.models.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<PageResponse<OrderDto>> getAllOrders(
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(orderService.getAll(pageable));
	}

	@GetMapping("/me")
	public ResponseEntity<PageResponse<OrderDto>> getMyOrders(
			@AuthenticationPrincipal User user,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(orderService.getByUserId(user.getId(), pageable));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDto> getOrderById(@PathVariable UUID orderId) {
		OrderDto order = orderService.getById(orderId);
		return ResponseEntity.ok(order);
	}

	@PostMapping("/checkout")
	public ResponseEntity<OrderDto> checkout(@RequestParam UUID userId, @Valid @RequestBody CheckoutRequest request) {
		return ResponseEntity.ok(orderService.checkout(userId, request));
	}

	@PostMapping("/{orderId}/cancel")
	public ResponseEntity<OrderDto> cancelOrder(@PathVariable UUID orderId, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(orderService.cancelOrder(orderId, user));
	}
}
