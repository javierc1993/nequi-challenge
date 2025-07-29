package com.example.franchise_api.infrastructure.adapters.persistenceadapter.mapper;

import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchPersistenceMapper {


    BranchEntity toEntity(Branch branch);
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "franchiseId", target = "franchiseId")
    @Mapping(target = "products", ignore = true)
    Branch toBranch(BranchEntity entity);
}
