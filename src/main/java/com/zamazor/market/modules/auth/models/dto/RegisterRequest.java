package com.zamazor.market.modules.auth.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.ZoneOffset;

public record RegisterRequest (
    @NotBlank @Email String email,
    @NotBlank @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&.]).{8,}$",
            message = "Password must be at least 8 characters long and include letters, numbers, and a special character."
    ) String password,
    @NotBlank @Size(min = 2) String name,
    @NotNull @Past LocalDate birthDate
) {
    @JsonIgnore
    @AssertTrue(message = "You must be 18 years or older to register.")
    @SuppressWarnings("unused")
    public boolean isAdult() {
        if (birthDate == null) return false;
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return !birthDate.plusYears(18).isAfter(today);
    }
}