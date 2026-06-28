package com.zamazor.market.modules.auth.models.dto;

public record TokenPair (
	String accessToken,
	String refreshToken
) { }
