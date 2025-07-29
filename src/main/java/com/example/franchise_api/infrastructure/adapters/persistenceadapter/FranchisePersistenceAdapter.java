package com.example.franchise_api.infrastructure.adapters.persistenceadapter;

import com.example.franchise_api.domain.model.Franchise;
import com.example.franchise_api.domain.spi.FranchiseRepositoryPort;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.mapper.FranchisePersistenceMapper;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchisePersistenceAdapter  implements FranchiseRepositoryPort {
    private final FranchiseRepository franchiseRepository;
    private final FranchisePersistenceMapper franchiseMapper;


    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity franchiseEntity = franchiseMapper.toEntity(franchise);
        return franchiseRepository.save(franchiseEntity)
                .map(franchiseMapper::toFranchise);
    }
}
