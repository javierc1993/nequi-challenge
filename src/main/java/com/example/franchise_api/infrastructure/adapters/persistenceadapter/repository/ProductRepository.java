package com.example.franchise_api.infrastructure.adapters.persistenceadapter.repository;

import com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, UUID> {
    Flux<ProductEntity> findByBranchIdAndActiveTrue(UUID branchId);
    Flux<ProductEntity> findByNameAndBranchIdAndActiveTrue(String name, UUID branchId);
}
