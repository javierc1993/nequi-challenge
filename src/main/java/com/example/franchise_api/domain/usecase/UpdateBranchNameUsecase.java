package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateBranchNameUsecase {
    private final BranchRepositoryPort branchRepositoryPort;

    public Mono<Branch> updateBranchName(UUID branchId, String newName) {
        // 1. Buscamos la sucursal existente.
        return branchRepositoryPort.findById(branchId)
                .switchIfEmpty(Mono.error(new RuntimeException("Branch not found with id: " + branchId)))
                // 2. Si existe, creamos una copia inmutable con el nuevo nombre.
                .flatMap(branch -> {
                    // La validación del nombre ocurre en el constructor del record.
                    Branch updatedBranch = new Branch(
                            branch.id(),
                            newName, // El nuevo nombre
                            branch.franchiseId(),
                            branch.products() // Mantenemos los productos existentes
                    );
                    // 3. Guardamos la sucursal actualizada.
                    return branchRepositoryPort.save(updatedBranch);
                });
    }
}
