package com.zamazor.market.modules.wishlist.models.dto;

import com.zamazor.market.modules.product.models.dto.ProductDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WishlistDto(
		UUID id,
		ProductDto product,
		LocalDateTime createdAt
) {
}