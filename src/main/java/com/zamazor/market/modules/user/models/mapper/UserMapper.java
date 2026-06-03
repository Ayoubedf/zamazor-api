package com.zamazor.market.modules.user.models.mapper;

import com.zamazor.market.modules.user.models.dto.UserDto;
import com.zamazor.market.modules.user.models.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
