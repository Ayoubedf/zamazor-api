package com.zamazor.market.modules.user.models.dto;

import com.zamazor.market.modules.user.models.entity.Role;

import java.util.UUID;

public record UserDto (
    UUID id,
    String email,
    String fullName,
    Role role
) { }