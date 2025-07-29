package com.example.franchise_api.infrastructure.adapters.persistenceadapter.mapper;

import org.mapstruct.Mapper;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import com.example.franchise_api.domain.model.Franchise;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FranchisePersistenceMapper {
    FranchiseEntity toEntity(Franchise franchise);
    @Mapping(target = "branches", ignore = true)
    Franchise toFranchise(FranchiseEntity entity);
}
