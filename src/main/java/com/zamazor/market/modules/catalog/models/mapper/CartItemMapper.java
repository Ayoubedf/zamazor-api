package com.zamazor.market.modules.catalog.models.mapper;

import com.zamazor.market.modules.catalog.models.dto.CartItemDto;
import com.zamazor.market.modules.catalog.models.entity.CartItem;
import com.zamazor.market.modules.product.models.mapper.ProductMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {
	CartItemDto toDto(CartItem cartItem);
}