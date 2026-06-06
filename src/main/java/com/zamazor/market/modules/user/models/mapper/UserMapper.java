package com.zamazor.market.modules.user.models.mapper;

import com.zamazor.market.modules.auth.models.dto.RegisterRequest;
import com.zamazor.market.modules.user.models.dto.UserDto;
import com.zamazor.market.modules.user.models.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterRequest request);
}
