package com.zamazor.market.shared.exception;

import com.zamazor.market.media.exception.MediaStorageException;
import com.zamazor.market.modules.auth.exception.EmailAlreadyExistsException;
import com.zamazor.market.modules.auth.exception.UnauthorizedException;
import com.zamazor.market.modules.catalog.exception.*;
import com.zamazor.market.modules.product.exception.CategoryNotFoundException;
import com.zamazor.market.modules.product.exception.OrderCancellationException;
import com.zamazor.market.modules.product.exception.OrderRefundException;
import com.zamazor.market.modules.product.exception.ProductNotFoundException;
import com.zamazor.market.modules.product.exception.CategoryAlreadyExistsException;
import com.zamazor.market.payment.exception.PaymentGatewayException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionResolver extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ExceptionResolver.class);

	@ExceptionHandler({
			ProductNotFoundException.class,
			CategoryNotFoundException.class,
			CartNotFoundException.class,
			OrderNotFoundException.class,
			AddressNotFoundException.class
	})
	public ProblemDetail handleNotFoundExceptions(RuntimeException ex) {
		return createProblemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
	}

	@ExceptionHandler({
			EmailAlreadyExistsException.class,
			CategoryAlreadyExistsException.class,
			OrderCancellationException.class,
			OrderRefundException.class,
			OutOfStockException.class
	})
	public ProblemDetail handleConflictExceptions(RuntimeException ex) {
		return createProblemDetail(HttpStatus.CONFLICT, "Business Rule Conflict", ex.getMessage());
	}

	@ExceptionHandler(EmptyCartException.class)
	public ProblemDetail handleBadRequestExceptions(RuntimeException ex) {
		return createProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Operation", ex.getMessage());
	}

	@ExceptionHandler({BadCredentialsException.class, UnauthorizedException.class})
	public ProblemDetail handleAuthentication(Exception ex) {
		return createProblemDetail(HttpStatus.UNAUTHORIZED, "Authentication Failed", ex.getMessage());
	}

	@ExceptionHandler({
			AccessDeniedException.class,
			AccountStatusException.class,
			SignatureException.class,
			ExpiredJwtException.class,
			UnauthorizedOrderException.class
	})
	public ProblemDetail handleAuthorization(Exception ex) {
		return createProblemDetail(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage());
	}

	@ExceptionHandler(PaymentGatewayException.class)
	public ProblemDetail handlePaymentGateway(PaymentGatewayException ex) {
		log.error("Payment Gateway Failure: ", ex);
		return createProblemDetail(HttpStatus.BAD_GATEWAY, "Payment Gateway Error", "Unable to process payment at this time.");
	}

	@ExceptionHandler(MediaStorageException.class)
	public ProblemDetail handleMediaStorage(MediaStorageException ex) {
		log.error("Media Storage Failure: ", ex);
		return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Storage Error", "Failed to process media asset.");
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleGenericException(Exception ex) {
		log.error("Unhandled exception encountered: ", ex);

		return createProblemDetail(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Internal Server Error",
				"An unexpected error occurred. Please contact support if the problem persists."
		);
	}

	private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
		problemDetail.setTitle(title);
		return problemDetail;
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			@NonNull HttpHeaders headers,
			@NonNull HttpStatusCode status,
			@NonNull WebRequest request
	) {
		Map<String, List<String>> fieldErrors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(Collectors.groupingBy(
						FieldError::getField,
						Collectors.mapping(
								DefaultMessageSourceResolvable::getDefaultMessage,
								Collectors.toList()
						)
				));

		ProblemDetail body = ProblemDetail.forStatusAndDetail(
				status,
				"One or more fields failed validation checks."
		);
		body.setTitle("Validation Failed");
		body.setProperty("invalid_params", fieldErrors);

		return createResponseEntity(body, headers, status, request);
	}
}
