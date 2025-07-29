package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.model.Franchise;
import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.FranchiseRepositoryPort;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import com.example.franchise_api.infrastructure.entrypoints.dto.BranchHighestStockReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetHighestStockProductReportUseCaseTests {
    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;
    @Mock
    private BranchRepositoryPort branchRepositoryPort;
    @Mock
    private ProductRepositoryPort productRepositoryPort;
    @InjectMocks
    private GetHighestStockProductReportUseCase getHighestStockProductReportUseCase;

    @Test
    void getReport_shouldReturnHighestStockProductPerBranch_whenDataExists() {
        // --- ARRANGE ---
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = new Franchise(franchiseId, "Test Franchise", null);

        Branch branch1 = new Branch(UUID.randomUUID(), "Branch 1", franchiseId, null);
        Branch branch2 = new Branch(UUID.randomUUID(), "Branch 2", franchiseId, null);

        Product p1_low = new Product(UUID.randomUUID(), "Product A", 50, true, branch1.id());
        Product p1_high = new Product(UUID.randomUUID(), "Product B", 100, true, branch1.id());
        Product p2_only = new Product(UUID.randomUUID(), "Product C", 75, true, branch2.id());

        when(franchiseRepositoryPort.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepositoryPort.findByFranchiseId(franchiseId)).thenReturn(Flux.just(branch1, branch2));
        when(productRepositoryPort.findByBranchIdAndActiveTrue(branch1.id())).thenReturn(Flux.just(p1_low, p1_high));
        when(productRepositoryPort.findByBranchIdAndActiveTrue(branch2.id())).thenReturn(Flux.just(p2_only));

        // --- ACT ---
        Flux<BranchHighestStockReport> resultFlux = getHighestStockProductReportUseCase.getReport(franchiseId);

        // --- ASSERT ---
        StepVerifier.create(resultFlux)
                .expectNextMatches(report ->
                        report.branchId().equals(branch1.id()) &&
                                report.productWithHighestStock().equals(p1_high) // Verifica que eligió el de stock 100
                )
                .expectNextMatches(report ->
                        report.branchId().equals(branch2.id()) &&
                                report.productWithHighestStock().equals(p2_only)
                )
                .verifyComplete();
    }

    @Test
    void getReport_shouldReturnEmptyFlux_whenFranchiseHasNoBranches() {
        // --- ARRANGE ---
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = new Franchise(franchiseId, "Test Franchise", null);

        when(franchiseRepositoryPort.findById(franchiseId)).thenReturn(Mono.just(franchise));
        // Simulamos que la franquicia no tiene sucursales
        when(branchRepositoryPort.findByFranchiseId(franchiseId)).thenReturn(Flux.empty());

        // --- ACT ---
        Flux<BranchHighestStockReport> resultFlux = getHighestStockProductReportUseCase.getReport(franchiseId);

        // --- ASSERT ---
        StepVerifier.create(resultFlux)
                .expectNextCount(0) // No esperamos ningún elemento en el reporte
                .verifyComplete();
    }

    @Test
    void getReport_shouldReturnReportWithNullProduct_whenBranchHasNoProducts() {
        // --- ARRANGE ---
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = new Franchise(franchiseId, "Test Franchise", null);
        Branch branchWithNoProducts = new Branch(UUID.randomUUID(), "Empty Branch", franchiseId, null);

        when(franchiseRepositoryPort.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepositoryPort.findByFranchiseId(franchiseId)).thenReturn(Flux.just(branchWithNoProducts));
        // Simulamos que la sucursal no tiene productos
        when(productRepositoryPort.findByBranchIdAndActiveTrue(branchWithNoProducts.id())).thenReturn(Flux.empty());

        // --- ACT ---
        Flux<BranchHighestStockReport> resultFlux = getHighestStockProductReportUseCase.getReport(franchiseId);

        // --- ASSERT ---
        StepVerifier.create(resultFlux)
                .expectNextMatches(report ->
                        report.branchId().equals(branchWithNoProducts.id()) &&
                                report.productWithHighestStock() == null // Verificamos que el producto es nulo
                )
                .verifyComplete();
    }

    @Test
    void getReport_shouldReturnError_whenFranchiseNotFound() {
        // --- ARRANGE ---
        UUID franchiseId = UUID.randomUUID();

        // Simulamos que la franquicia no se encuentra
        when(franchiseRepositoryPort.findById(franchiseId)).thenReturn(Mono.empty());

        // --- ACT ---
        Flux<BranchHighestStockReport> resultFlux = getHighestStockProductReportUseCase.getReport(franchiseId);

        // --- ASSERT ---
        StepVerifier.create(resultFlux)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.FRANCHISE_NOT_FOUND
                )
                .verify();
    }
}
