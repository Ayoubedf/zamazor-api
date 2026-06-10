package com.zamazor.market.modules.product.models.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(
    UUID id,
    String name,
    String imageUrl,
    BigDecimal basePrice,
    UUID categoryId,
    String categoryLabel
) { }
