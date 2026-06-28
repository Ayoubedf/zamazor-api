package com.zamazor.market.modules.catalog.models.mapper;

import com.zamazor.market.modules.catalog.models.dto.OrderItemDto;
import com.zamazor.market.modules.catalog.models.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
	@Mapping(source = "productId", target = "product.id")
	@Mapping(source = "productName", target = "product.name")
	@Mapping(source = "productImageUrl", target = "product.imageUrl")
	@Mapping(source = "unitPrice", target = "product.price")
	OrderItemDto toDto(OrderItem orderItem);
}