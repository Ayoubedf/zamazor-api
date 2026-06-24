package com.zamazor.market.infrastructure.media.cloudinary;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "cloudinary")
public record CloudinaryProperties(
        @Valid @NotBlank String cloudName,
        @Valid @NotBlank String apiKey,
        @Valid @NotBlank String apiSecret
) { }
