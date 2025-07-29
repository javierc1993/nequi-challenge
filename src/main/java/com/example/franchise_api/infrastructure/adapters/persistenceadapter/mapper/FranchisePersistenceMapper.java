package com.example.franchise_api.infrastructure.adapters.persistenceadapter.mapper;

import org.mapstruct.Mapper;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import com.example.franchise_api.domain.model.Franchise;

@Mapper(componentModel = "spring")
public interface FranchisePersistenceMapper {
    FranchiseEntity toEntity(Franchise franchise);
    Franchise toFranchise(FranchiseEntity entity);
}
