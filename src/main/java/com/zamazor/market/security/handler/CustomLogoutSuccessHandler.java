package com.zamazor.market.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(
			@NonNull HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) {
		response.setStatus(HttpServletResponse.SC_OK);
	}
}