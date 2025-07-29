package com.example.franchise_api.infrastructure.adapters.persistenceadapter.mapper;

import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {
    ProductEntity toEntity(Product product);
    Product toProduct(ProductEntity entity);
}
