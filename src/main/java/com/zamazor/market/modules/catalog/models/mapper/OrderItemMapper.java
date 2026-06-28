package com.zamazor.market.modules.catalog.models.mapper;

import com.zamazor.market.modules.catalog.models.dto.OrderItemDto;
import com.zamazor.market.modules.catalog.models.entity.OrderItem;
import com.zamazor.market.modules.product.models.mapper.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		uses = {ProductMapper.class},
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderItemMapper {
	OrderItemDto toDto(OrderItem cartItem);
}