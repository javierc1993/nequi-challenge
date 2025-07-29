package com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper;

import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchPersistenceMapper {


    BranchEntity toEntity(Branch branch);
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "franchiseId", target = "franchiseId")
    Branch toBranch(BranchEntity entity);
}
