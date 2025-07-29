package com.example.franchise_api.infrastructure.entrypoints.mapper;

import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.infrastructure.entrypoints.dto.CreateProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductRestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true) // Lo manejamos en el UseCase
    @Mapping(target = "branchId", ignore = true) // Lo manejamos en el UseCase
    Product toProduct(CreateProductRequest dto);
}
