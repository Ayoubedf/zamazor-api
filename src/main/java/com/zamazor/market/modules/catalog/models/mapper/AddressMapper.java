package com.zamazor.market.modules.catalog.models.mapper;

import com.zamazor.market.modules.catalog.models.dto.AddressDto;
import com.zamazor.market.modules.catalog.models.dto.AddressRequest;
import com.zamazor.market.modules.catalog.models.entity.Address;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "addressDetails.country", source = "request.country")
	@Mapping(target = "addressDetails.city", source = "request.city")
	@Mapping(target = "addressDetails.street", source = "request.street")
	@Mapping(target = "addressDetails.phone", source = "request.phone")
	Address toEntity(AddressRequest request);

	@BeanMapping(ignoreByDefault = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "addressDetails.country", source = "request.country")
	@Mapping(target = "addressDetails.city", source = "request.city")
	@Mapping(target = "addressDetails.street", source = "request.street")
	@Mapping(target = "addressDetails.phone", source = "request.phone")
	void update(AddressRequest request, @MappingTarget Address address);

	@BeanMapping(ignoreByDefault = true)
	@Mapping(target = "id", source = "id")
	@Mapping(target = "country", source = "addressDetails.country")
	@Mapping(target = "city", source = "addressDetails.city")
	@Mapping(target = "street", source = "addressDetails.street")
	@Mapping(target = "phone", source = "addressDetails.phone")
	AddressDto toDto(Address address);
}