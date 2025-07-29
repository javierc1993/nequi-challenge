package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
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
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND , franchiseId)))
                .flatMapMany(franchise -> branchRepositoryPort.findByFranchiseId(franchise.id()))
                .flatMap(branch -> {
                    Mono<Product> highestStockProductMono = productRepositoryPort.findByBranchIdAndActiveTrue(branch.id())
                            .reduce((product1, product2) ->
                                    product1.stock() >= product2.stock() ? product1 : product2
                            );

                    return highestStockProductMono
                            .map(product -> new BranchHighestStockReport(branch.id(), branch.name(), product))
                            .switchIfEmpty(Mono.just(
                                    new BranchHighestStockReport(branch.id(), branch.name(), null)
                            ));
                });
}

}