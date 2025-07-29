package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Franchise;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateFranchiseNameUseCase {
    private final FranchiseRepositoryPort franchiseRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;

    public Mono<Franchise> updateFranchiseName(UUID franchiseId, String newName) {

        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND , franchiseId)))

                .flatMap(franchise -> {
                    Franchise updatedFranchise = new Franchise(
                            franchise.id(),
                            newName,
                            franchise.branches()
                    );
                    return franchiseRepositoryPort.save(updatedFranchise);
                })
                .flatMap(savedFranchise ->
                        branchRepositoryPort.findByFranchiseId(savedFranchise.id())
                                .collectList()
                                .map(branches -> new Franchise(
                                        savedFranchise.id(),
                                        savedFranchise.name(),
                                        branches
                                ))
                );
    }
}
