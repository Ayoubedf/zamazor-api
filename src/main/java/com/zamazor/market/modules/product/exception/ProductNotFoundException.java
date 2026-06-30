package com.zamazor.market.modules.product.exception;

import com.zamazor.market.shared.exception.DomainException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ProductNotFoundException extends DomainException {
	public ProductNotFoundException(UUID id) {
		super("Product not found with id: " + id, HttpStatus.NOT_FOUND, "Product Not Found");
	}
}