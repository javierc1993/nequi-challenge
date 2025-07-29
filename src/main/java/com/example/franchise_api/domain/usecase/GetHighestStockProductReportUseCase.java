package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.FranchiseRepositoryPort;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import com.example.franchise_api.infrastructure.entrypoints.dto.BranchHighestStockReport;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.UUID;

@RequiredArgsConstructor
public class GetHighestStockProductReportUseCase {
    private final FranchiseRepositoryPort franchiseRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public Flux<BranchHighestStockReport> getReport(UUID franchiseId) {

        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found with id: " + franchiseId)))

                .flatMapMany(franchise -> branchRepositoryPort.findByFranchiseId(franchise.id()))

                .flatMap(branch -> {
                    // Paso A: Obtenemos el Mono que PUEDE contener el producto con mayor stock.
                    Mono<Product> highestStockProductMono = productRepositoryPort.findByBranchIdAndActiveTrue(branch.id())
                            .reduce((product1, product2) ->
                                    product1.stock() >= product2.stock() ? product1 : product2
                            );

                    // Paso B: Definimos qué hacer en cada caso.
                    return highestStockProductMono
                            // Camino 1: Si el Mono tiene un producto, crea el reporte con él.
                            .map(product -> new BranchHighestStockReport(branch.id(), branch.name(), product))
                            // Camino 2: Si el Mono está vacío, "cámbiate" a un Mono que emita el reporte con un producto nulo.
                            .switchIfEmpty(Mono.just(
                                    new BranchHighestStockReport(branch.id(), branch.name(), null)
                            ));
                });
}

}