package com.zamazor.market.modules.catalog.models.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartDto(
		UUID id,
		UUID userId,
		BigDecimal total,
		List<CartItemDto> items
) {}