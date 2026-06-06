package com.zamazor.market.modules.user.models.dto;

import com.zamazor.market.modules.user.models.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String name;
    private String avatarUrl;
    private LocalDate birthDate;
    private Role role;
}