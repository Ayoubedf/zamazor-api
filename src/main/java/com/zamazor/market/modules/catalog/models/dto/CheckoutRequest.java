package com.zamazor.market.modules.catalog.models.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
		@NotBlank String shippingAddress
) {}