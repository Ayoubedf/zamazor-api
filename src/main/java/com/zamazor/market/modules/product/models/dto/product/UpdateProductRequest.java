package com.zamazor.market.modules.product.models.dto.product;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
    @Nullable @Size(min = 2, max = 200) String name,
    @Nullable @Size(max = 2000) String description,
    @Nullable @URL String imageUrl,
    @Nullable @Positive BigDecimal basePrice,
    @Nullable UUID categoryId
) { }
