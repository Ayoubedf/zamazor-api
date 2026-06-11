package com.zamazor.market.modules.product.models.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CategoryDto(
		@NotNull UUID id,
		@NotNull String label
) { }