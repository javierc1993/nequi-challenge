package com.example.franchise_api.infrastructure.adapters.persistenceadapter.repository;

import com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface BranchRepository  extends ReactiveCrudRepository<BranchEntity, UUID> {
    Flux<BranchEntity> findByFranchiseId(UUID franchiseId);
}
