package com.zamazor.market.modules.catalog.models.mapper;

import com.zamazor.market.modules.catalog.models.entity.Cart;
import com.zamazor.market.modules.catalog.models.dto.CartDto;
import com.zamazor.market.modules.catalog.models.dto.CartItemDto;
import com.zamazor.market.modules.catalog.models.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
	@Mapping(target = "userId", source = "user.id")
	CartDto toDto(Cart order);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "items", ignore = true)
	Cart toEntity(CartDto dto);

	CartItemDto toCartItemDto(CartItem orderItem);
}
