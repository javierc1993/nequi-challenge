package com.example.franchise_api.infrastructure.entrypoints.mapper;

import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.model.Franchise;
import com.example.franchise_api.infrastructure.entrypoints.dto.CreateBranchRequest;
import com.example.franchise_api.infrastructure.entrypoints.dto.CreateFranchiseRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FranchiseRestMapper {
    @Mapping(target = "id", ignore = true)
    Franchise toFranchise(CreateFranchiseRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "franchiseId", ignore = true)
    @Mapping(target = "products", ignore = true)
    Branch toBranch(CreateBranchRequest request);
}
