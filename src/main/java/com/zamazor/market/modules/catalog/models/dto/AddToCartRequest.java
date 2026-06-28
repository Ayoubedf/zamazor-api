package com.zamazor.market.modules.catalog.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddToCartRequest(
		@NotNull UUID productId,
		@NotNull @Positive Integer quantity
) {}