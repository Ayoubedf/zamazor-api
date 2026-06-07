package com.zamazor.market.modules.auth.models.dto;

import com.zamazor.market.modules.user.models.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResult {
	private String refreshToken;
	private String accessToken;
	private UserDto user;
}
