package com.zamazor.market.config;

import io.jsonwebtoken.ExpiredJwtException;
import org.jspecify.annotations.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionResolver extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception){
        return switch (exception) {
            case BadCredentialsException e -> createProblemDetail(401, e.getMessage(), "Invalid Credentials");
            case AccountStatusException e -> createProblemDetail(403, e.getMessage(), "Account Locked");
            case AccessDeniedException e -> createProblemDetail(403, e.getMessage(), "Unauthorized to access this resource");
            case SignatureException e -> createProblemDetail(403, e.getMessage(), "Invalid JWT Signature");
            case ExpiredJwtException e -> createProblemDetail(403, e.getMessage(), "JWT token has expired");
            default -> createProblemDetail(401, exception.getMessage(), "Unknown internal server error.");
        };
    }

    private ProblemDetail createProblemDetail(int status, String message, String description) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(status), message);
        detail.setProperty("description", description);
        return detail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
		    MethodArgumentNotValidException ex,
		    @NonNull HttpHeaders headers,
		    @NonNull HttpStatusCode status,
		    @NonNull WebRequest request) {

        String errorDetails = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));

        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, errorDetails);
        body.setTitle("Validation Failed");
        body.setProperty("description", "One or more fields failed validation checks.");

        return this.createResponseEntity(body, headers, status, request);
    }
}
