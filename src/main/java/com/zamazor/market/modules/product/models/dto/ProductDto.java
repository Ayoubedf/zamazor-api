package com.zamazor.market.modules.product.models.dto;

import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
		@NotNull UUID id,
		@NotNull String name,
		@Nullable String description,
		@Nullable String imageUrl,
		@NotNull BigDecimal price,
		@NotNull Integer stockQuantity,
		@NotNull Integer reservedQuantity,
		@NotNull CategoryDto category
) { }
