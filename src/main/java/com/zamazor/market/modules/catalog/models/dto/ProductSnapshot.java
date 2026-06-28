package com.zamazor.market.modules.catalog.models.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSnapshot(
		UUID id,
		String name,
		String imageUrl,
		BigDecimal price
) {
}
