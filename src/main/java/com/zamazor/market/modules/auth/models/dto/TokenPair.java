package com.zamazor.market.modules.auth.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenPair {
	private String accessToken;
	private String refreshToken;
}
