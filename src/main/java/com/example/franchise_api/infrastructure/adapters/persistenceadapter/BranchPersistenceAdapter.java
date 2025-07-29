package com.example.franchise_api.infrastructure.adapters.persistenceadapter;

import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.mapper.BranchPersistenceMapper;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BranchPersistenceAdapter implements BranchRepositoryPort {
    private final BranchRepository branchR2dbcRepository;
    private final BranchPersistenceMapper branchMapper;


    @Override
    public Mono<Branch> save(Branch branch) {
        BranchEntity branchEntity = branchMapper.toEntity(branch);
        return branchR2dbcRepository.save(branchEntity)
                .map(branchMapper::toBranch);
    }

    @Override
    public Flux<Branch> findByFranchiseId(UUID franchiseId) {
        return branchR2dbcRepository.findByFranchiseId(franchiseId)
                .map(branchMapper::toBranch);
    }

    @Override
    public Mono<Branch> findById(UUID id) {
        return branchR2dbcRepository.findById(id)
                .map(branchMapper::toBranch);
    }
}
