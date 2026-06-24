package com.zamazor.market.modules.product.models.mapper;

import com.zamazor.market.modules.product.models.dto.CategoryDto;
import com.zamazor.market.modules.product.models.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	CategoryDto toDto(Category category);

	@Mapping(target = "id", ignore = true)
	Category toEntity(CategoryDto categoryDto);
}
