package com.zamazor.market.modules.auth.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthenticationRequest (
    @NotBlank @Email String email,
    @NotBlank @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&.]).{8,}$",
            message = "Password must be at least 8 characters long and include letters, numbers, and a special character."
    ) String password
) { }
