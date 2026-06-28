package com.zamazor.market.modules.product.models.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
		@NotBlank String label
) {
}
