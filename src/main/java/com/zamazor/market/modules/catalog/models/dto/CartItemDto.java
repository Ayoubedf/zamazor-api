package com.zamazor.market.modules.catalog.models.dto;

import com.zamazor.market.modules.product.models.dto.ProductDto;

import java.util.UUID;

public record CartItemDto(
		UUID id,
		ProductDto product,
		Integer quantity
) {}