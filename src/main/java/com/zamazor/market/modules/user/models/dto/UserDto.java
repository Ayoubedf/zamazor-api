package com.zamazor.market.modules.user.models.dto;

import com.zamazor.market.modules.user.models.entity.Role;

import java.time.LocalDate;
import java.util.UUID;

public record UserDto (
    UUID id,
    String email,
    String name,
    String avatarUrl,
    LocalDate birthDate,
    Role role
) { }