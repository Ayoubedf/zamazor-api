package com.zamazor.market.modules.catalog.models.mapper;

import com.zamazor.market.modules.catalog.models.dto.OrderDto;
import com.zamazor.market.modules.catalog.models.dto.OrderItemDto;
import com.zamazor.market.modules.catalog.models.entity.Order;
import com.zamazor.market.modules.catalog.models.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
	@Mapping(target = "userId", source = "user.id")
	OrderDto toDto(Order order);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "items", ignore = true)
	Order toEntity(OrderDto dto);

	OrderItemDto toOrderItemDto(OrderItem orderItem);
}
