package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.infrastructure.entrypoints.dto.CreateFranchiseRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseRestMapper {
    Franchise toFranchise(CreateFranchiseRequest request);
}
