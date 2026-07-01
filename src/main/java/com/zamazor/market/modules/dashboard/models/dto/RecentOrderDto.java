package com.zamazor.market.modules.dashboard.models.dto;

import com.zamazor.market.modules.catalog.models.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RecentOrderDto(
		UUID id,
		BigDecimal total,
		OrderStatus status,
		String shippingCountry,
		String shippingCity,
		String shippingStreet,
		String phone,
		Instant createdAt
) {
}