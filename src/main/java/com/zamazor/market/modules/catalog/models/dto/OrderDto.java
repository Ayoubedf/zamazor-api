package com.zamazor.market.modules.catalog.models.dto;

import com.zamazor.market.modules.catalog.models.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDto(
		UUID id,
		OrderStatus status,
		BigDecimal total,
		List<OrderItemDto> items,
		String shippingCountry,
		String shippingCity,
		String shippingStreet,
		String phone,
		UserMinDto user,
		LocalDateTime createdAt
) {
}