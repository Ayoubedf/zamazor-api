package com.zamazor.market.modules.auth.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;

@Getter
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&.]).{8,}$",
            message = "Password must be at least 8 characters long and include letters, numbers, and a special character."
    )
    private String password;

    @NotBlank
    @Size(min = 2)
    private String name;

    @NotNull
    @Past
    private LocalDate birthDate;

    @JsonIgnore
    @AssertTrue(message = "You must be 18 years or older to register.")
    public boolean isAdult() {
        if (birthDate == null) return false;
        return Period.between(birthDate, LocalDate.now(ZoneOffset.UTC)).getYears() >= 18;
    }
}