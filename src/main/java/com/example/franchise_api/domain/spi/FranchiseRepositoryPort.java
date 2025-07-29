package com.example.franchise_api.domain.spi;

import com.example.franchise_api.domain.model.Franchise;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FranchiseRepositoryPort {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(UUID id);
}
