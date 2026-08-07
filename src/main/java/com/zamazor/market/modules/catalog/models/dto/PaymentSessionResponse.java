package com.zamazor.market.modules.catalog.models.dto;

import java.time.Instant;

public record PaymentSessionResponse(
		String paymentUrl,
		String sessionId,
		Instant expiresAt
) {
}
