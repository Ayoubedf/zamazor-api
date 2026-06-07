package com.zamazor.market.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "spring.security.jwt")
public record JwtProperties(
    @Valid @NotNull Access access,
    @Valid @NotNull Refresh refresh,
    @NotBlank String issuer
) {
    public record Access(
        @Size(min = 32) String secret,
        @DurationMin(minutes = 1) @NotNull Duration expiration
    ) {}

    public record Refresh(
        @Size(min = 32) String secret,
        @DurationMin(hours = 1) @NotNull Duration expiration
    ) {}
}