package com.zamazor.market.modules.auth.models.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest (
    @NotBlank @Email String email,
    @NotBlank @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&.]).{8,}$",
            message = "Password must be at least 8 characters long and include letters, numbers, and a special character."
    ) String password,
    @NotBlank @Size(min = 2) String fullName
) { }