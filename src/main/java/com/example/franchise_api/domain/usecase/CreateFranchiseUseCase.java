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
                new ArrayList<>() // La lista de sucursales se llenará después de persistir
        );
        return franchiseRepositoryPort.save(franchiseToSave)
                .flatMap(savedFranchise -> {
                    // 3. Una vez guardada, tenemos el `savedFranchise` con su ID.
                    // Ahora, creamos un flujo (Flux) a partir de la lista de sucursales que venían en la petición.
                    return Flux.fromIterable(franchiseWithBranches.branches())
                            // 4. Para cada sucursal, creamos un nuevo objeto `Branch` con el ID de la franquicia padre.
                            .map(branch -> new Branch(
                                    null,
                                    branch.name(),
                                    savedFranchise.id(), // <-- ¡Asignamos la clave foránea!
                                    new ArrayList<>()))
                            // 5. Guardamos cada sucursal en la base de datos de forma reactiva.
                            .flatMap(branchRepositoryPort::save)
                            // 6. Recolectamos todas las sucursales guardadas en una lista.
                            .collectList()
                            // 7. Finalmente, creamos el objeto `Franchise` de respuesta completo.
                            .map(savedBranches -> new Franchise(
                                    savedFranchise.id(),
                                    savedFranchise.name(),
                                    savedBranches // <-- La lista de sucursales ya persistidas
                            ));
                });
    }

}
