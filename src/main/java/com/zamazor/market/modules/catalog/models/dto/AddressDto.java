package com.zamazor.market.modules.catalog.models.dto;

import java.util.UUID;

public record AddressDto(
		UUID id,
		String country,
		String city,
		String street,
		String phone
) {
}