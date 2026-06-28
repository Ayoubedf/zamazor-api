package com.zamazor.market.modules.product.exception;

public class OrderCancellationException extends RuntimeException {
	public OrderCancellationException(String message) {
		super(message);
	}
}
