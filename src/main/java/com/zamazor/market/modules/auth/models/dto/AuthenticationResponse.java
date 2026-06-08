package com.zamazor.market.modules.auth.models.dto;

import com.zamazor.market.modules.user.models.dto.UserDto;

public record AuthenticationResponse (
    String accessToken,
    UserDto user
) { }
