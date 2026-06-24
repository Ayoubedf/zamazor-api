package com.zamazor.market.modules.catalog.models.dto;

import com.zamazor.market.modules.product.models.dto.ProductDto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
		UUID id,
		BigDecimal unitPrice,
		ProductDto product,
		Integer quantity
) {}