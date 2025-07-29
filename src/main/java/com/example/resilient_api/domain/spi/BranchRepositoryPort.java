package com.example.resilient_api.domain.spi;
import com.example.resilient_api.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BranchRepositoryPort {
    Mono<Branch> save(Branch branch);
    Flux<Branch> findByFranchiseId(UUID franchiseId);
}
