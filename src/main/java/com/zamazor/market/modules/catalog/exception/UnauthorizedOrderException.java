package com.zamazor.market.modules.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedOrderException extends RuntimeException {
	public UnauthorizedOrderException(String message) {
		super(message);
	}
}
