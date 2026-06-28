package com.zamazor.market.modules.catalog.models.mapper;

import com.zamazor.market.modules.catalog.models.dto.OrderDto;
import com.zamazor.market.modules.catalog.models.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		uses = {OrderItemMapper.class},
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {
	OrderDto toDto(Order order);
}
