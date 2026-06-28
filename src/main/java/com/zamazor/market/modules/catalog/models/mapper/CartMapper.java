package com.zamazor.market.modules.catalog.models.mapper;

import com.zamazor.market.modules.catalog.models.entity.Cart;
import com.zamazor.market.modules.catalog.models.dto.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
	CartDto toDto(Cart cart);
}
