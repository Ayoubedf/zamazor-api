package com.zamazor.market.modules.catalog.models.dto;

import java.util.UUID;

public record StockRestoreDto(UUID productId, Integer quantity) {
}