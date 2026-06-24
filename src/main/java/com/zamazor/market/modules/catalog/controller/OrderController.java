package com.zamazor.market.modules.catalog.controller;

import com.zamazor.market.modules.catalog.models.dto.CheckoutRequest;
import com.zamazor.market.modules.catalog.models.dto.OrderDto;
import com.zamazor.market.modules.catalog.service.OrderService;
import com.zamazor.market.modules.user.models.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<List<OrderDto>> getAllOrders() {
		return ResponseEntity.ok(orderService.getAll());
	}

	@GetMapping("/me")
	public ResponseEntity<List<OrderDto>> getMyOrders(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(orderService.getByUserId(user.getId()));
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
