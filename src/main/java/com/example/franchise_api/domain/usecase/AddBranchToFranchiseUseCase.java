package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
public class AddBranchToFranchiseUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;

    public Mono<Branch> addBranch(UUID franchiseId, Branch newBranch) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND, franchiseId)))
                .flatMap(franchise -> {
                    Branch branchToSave = new Branch(
                            null,
                            newBranch.name(),
                            franchise.id(),
                            new ArrayList<>()
                    );
                    return branchRepositoryPort.save(branchToSave);
                });
    }

}
