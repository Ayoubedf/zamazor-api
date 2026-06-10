package com.zamazor.market.modules.product.models.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDetailResponse(
    UUID id,
    String name,
    String description,
    String imageUrl,
    BigDecimal basePrice,
    UUID categoryId,
    String categoryLabel
) { }
