package com.example.franchise_api.domain.usecase;

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
        // 1. Buscamos la franquicia existente.
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found with id: " + franchiseId)))
                // 2. Si existe, creamos una copia inmutable con el nuevo nombre.
                .flatMap(franchise -> {
                    // La validación del nombre ocurre en el constructor del record.
                    Franchise updatedFranchise = new Franchise(
                            franchise.id(),
                            newName, // El nuevo nombre
                            franchise.branches() // Mantenemos las sucursales existentes
                    );
                    // 3. Guardamos la franquicia actualizada.
                    return franchiseRepositoryPort.save(updatedFranchise);
                })
                // 4. El save devuelve una franquicia sin la lista de sucursales,
                //    así que las volvemos a cargar para dar una respuesta completa.
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
