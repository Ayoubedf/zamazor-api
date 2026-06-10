package com.zamazor.market.modules.product.models.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
    @NotBlank @Size(min = 2, max = 200) String name,
    @NotBlank @Size(max = 2000) String description,
    @NotBlank @URL String imageUrl,
    @NotNull @Positive BigDecimal basePrice,
    @NotNull UUID categoryId
) { }
