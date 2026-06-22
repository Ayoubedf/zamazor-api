package com.zamazor.market.modules.product.models.mapper;

import com.zamazor.market.modules.product.models.dto.CreateProductRequest;
import com.zamazor.market.modules.product.models.dto.ProductDto;
import com.zamazor.market.modules.product.models.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "reservedQuantity", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "version", ignore = true)
    Product toEntity(CreateProductRequest request);
}
