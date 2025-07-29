package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.model.Franchise;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;


@RequiredArgsConstructor
public class CreateFranchiseUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;


    public Mono<Franchise> create(Franchise franchiseWithBranches) {

        Franchise franchiseToSave = new Franchise(
                null,
                franchiseWithBranches.name(),
                new ArrayList<>()
        );
        return franchiseRepositoryPort.save(franchiseToSave)
                .flatMap(savedFranchise -> {
                    return Flux.fromIterable(franchiseWithBranches.branches())
                            .map(branch -> new Branch(
                                    null,
                                    branch.name(),
                                    savedFranchise.id(),
                                    new ArrayList<>()))
                            .flatMap(branchRepositoryPort::save)
                            .collectList()
                            .map(savedBranches -> new Franchise(
                                    savedFranchise.id(),
                                    savedFranchise.name(),
                                    savedBranches
                            ));
                });
    }

}
