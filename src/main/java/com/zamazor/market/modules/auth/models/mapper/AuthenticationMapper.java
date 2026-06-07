package com.zamazor.market.modules.auth.models.mapper;

import com.zamazor.market.modules.auth.models.dto.AuthenticationResponse;
import com.zamazor.market.modules.auth.models.dto.AuthenticationResult;
import com.zamazor.market.modules.auth.models.dto.TokenPair;
import com.zamazor.market.modules.auth.models.dto.TokenRefreshResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {
	TokenRefreshResponse toRefreshResponse(TokenPair tokenPair);

	AuthenticationResponse toAuthenticationResponse(AuthenticationResult authenticationResult);
}