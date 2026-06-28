package com.zamazor.market.modules.catalog.models.dto;

import com.zamazor.market.modules.catalog.models.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
		@NotNull OrderStatus status
) {
}
