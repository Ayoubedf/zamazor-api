package com.zamazor.market.modules.catalog.models.dto;

import jakarta.validation.constraints.Positive;
import org.jspecify.annotations.NonNull;

public record UpdateQuantityRequest(@NonNull @Positive Integer quantity) {}