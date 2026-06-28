package com.zamazor.market.modules.auth.models.dto;

import com.zamazor.market.modules.user.models.dto.UserDto;

public record AuthenticationResult (
		String refreshToken,
		String accessToken,
		UserDto user
) { }
