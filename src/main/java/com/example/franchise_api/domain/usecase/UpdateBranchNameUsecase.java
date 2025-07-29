package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateBranchNameUsecase {
    private final BranchRepositoryPort branchRepositoryPort;

    public Mono<Branch> updateBranchName(UUID branchId, String newName) {
        return branchRepositoryPort.findById(branchId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND, branchId)))
                .flatMap(branch -> {

                    Branch updatedBranch = new Branch(
                            branch.id(),
                            newName,
                            branch.franchiseId(),
                            branch.products()
                    );
                    return branchRepositoryPort.save(updatedBranch);
                });
    }
}
