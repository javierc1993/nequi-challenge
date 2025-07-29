package com.example.franchise_api.domain.usecase;

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
        // 1. Verificamos que la franquicia padre exista.
        return franchiseRepositoryPort.findById(franchiseId)
                // 2. Si no existe, findById emite un Mono vacío.
                // `switchIfEmpty` nos permite lanzar un error en ese caso.
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found with id: " + franchiseId)))
                // 3. Si existe, `flatMap` nos permite continuar el flujo.
                .flatMap(franchise -> {
                    // 4. Creamos el objeto Branch completo con el franchiseId y un ID nuevo.
                    Branch branchToSave = new Branch(
                            null, // El ID se genera en la BD
                            newBranch.name(),
                            franchise.id(), // Usamos el ID de la franquicia encontrada
                            new ArrayList<>()
                    );
                    // 5. Guardamos la nueva sucursal usando su propio puerto.
                    return branchRepositoryPort.save(branchToSave);
                });
    }

}
