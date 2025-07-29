package com.example.franchise_api.infrastructure.adapters.persistenceadapter.repository;

import com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface FranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, UUID> {
}
