package com.example.franchise_api.infrastructure.entrypoints.mapper;

import com.example.franchise_api.domain.model.Franchise;
import com.example.franchise_api.infrastructure.entrypoints.dto.CreateFranchiseRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseRestMapper {
    Franchise toFranchise(CreateFranchiseRequest request);
}
