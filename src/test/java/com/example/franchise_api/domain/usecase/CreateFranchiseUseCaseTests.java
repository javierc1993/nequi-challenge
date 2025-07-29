package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.model.Franchise;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.FranchiseRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateFranchiseUseCaseTests {
    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;
    @Mock
    private BranchRepositoryPort branchRepositoryPort;
    @InjectMocks
    private CreateFranchiseUseCase createFranchiseUseCase;

    @Test
    void create_shouldSaveFranchiseAndBranches_whenBranchesAreProvided() {
        // --- ARRANGE ---
        // 1. Datos de entrada
        Branch inputBranch1 = new Branch(null, "Downtown Branch", null, null);
        Branch inputBranch2 = new Branch(null, "Uptown Branch", null, null);
        Franchise inputFranchise = new Franchise(null, "Super Burgers", List.of(inputBranch1, inputBranch2));

        // 2. Datos simulados de la persistencia
        UUID franchiseId = UUID.randomUUID();
        Franchise savedFranchiseShell = new Franchise(franchiseId, "Super Burgers", new ArrayList<>());
        Branch savedBranch1 = new Branch(UUID.randomUUID(), "Downtown Branch", franchiseId, new ArrayList<>());
        Branch savedBranch2 = new Branch(UUID.randomUUID(), "Uptown Branch", franchiseId, new ArrayList<>());

        // 3. Comportamiento de los Mocks
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchiseShell));
        when(branchRepositoryPort.save(any(Branch.class)))
                .thenReturn(Mono.just(savedBranch1)) // Primera llamada a save()
                .thenReturn(Mono.just(savedBranch2)); // Segunda llamada a save()

        // --- ACT ---
        Mono<Franchise> resultMono = createFranchiseUseCase.create(inputFranchise);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectNextMatches(finalFranchise ->
                        finalFranchise.name().equals("Super Burgers") &&
                                finalFranchise.branches().size() == 2 &&
                                finalFranchise.branches().contains(savedBranch1) &&
                                finalFranchise.branches().contains(savedBranch2)
                )
                .verifyComplete();

        verify(franchiseRepositoryPort, times(1)).save(any(Franchise.class));
        verify(branchRepositoryPort, times(2)).save(any(Branch.class));
    }

    @Test
    void create_shouldSaveFranchiseWithEmptyBranches_whenNoBranchesAreProvided() {
        // --- ARRANGE ---
        Franchise inputFranchise = new Franchise(null, "Solo Burgers", new ArrayList<>());
        UUID franchiseId = UUID.randomUUID();
        Franchise savedFranchise = new Franchise(franchiseId, "Solo Burgers", new ArrayList<>());

        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        // --- ACT ---
        Mono<Franchise> resultMono = createFranchiseUseCase.create(inputFranchise);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectNextMatches(finalFranchise ->
                        finalFranchise.name().equals("Solo Burgers") &&
                                finalFranchise.branches().isEmpty()
                )
                .verifyComplete();

        verify(branchRepositoryPort, never()).save(any(Branch.class));
    }

    @Test
    void create_shouldReturnError_whenFranchiseSaveFails() {
        // --- ARRANGE ---
        Franchise inputFranchise = new Franchise(null, "Error Burgers", new ArrayList<>());

        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        // --- ACT ---
        Mono<Franchise> resultMono = createFranchiseUseCase.create(inputFranchise);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Database connection failed")
                )
                .verify();
    }

    @Test
    void create_shouldReturnError_whenBranchSaveFails() {
        // --- ARRANGE ---
        Branch inputBranch = new Branch(null, "Branch", null, null);
        Franchise inputFranchise = new Franchise(null, "Super Burgers", List.of(inputBranch));

        UUID franchiseId = UUID.randomUUID();
        Franchise savedFranchiseShell = new Franchise(franchiseId, "Super Burgers", new ArrayList<>());

        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchiseShell));
        when(branchRepositoryPort.save(any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("Failed to save branch")));

        // --- ACT ---
        Mono<Franchise> resultMono = createFranchiseUseCase.create(inputFranchise);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Failed to save branch")
                )
                .verify();
    }
}
