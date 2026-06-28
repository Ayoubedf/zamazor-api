package com.zamazor.market.modules.catalog.models.dto;

import java.util.UUID;

public record OrderItemDto(
		UUID id,
		ProductSnapshot product,
		Integer quantity
) {
}