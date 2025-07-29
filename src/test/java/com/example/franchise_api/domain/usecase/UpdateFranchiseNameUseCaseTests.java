package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.model.Franchise;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.FranchiseRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateFranchiseNameUseCaseTests {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;
    @Mock
    private BranchRepositoryPort branchRepositoryPort;
    @InjectMocks
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @Test
    void updateFranchiseName_shouldUpdateAndReturnCompleteFranchise_whenFranchiseExists() {
        // --- ARRANGE ---
        UUID franchiseId = UUID.randomUUID();
        String newName = "Nuevo Nombre de Franquicia";

        // 1. Datos iniciales
        Branch branch1 = new Branch(UUID.randomUUID(), "Sucursal A", franchiseId, List.of());
        Franchise existingFranchise = new Franchise(franchiseId, "Nombre Antiguo", List.of(branch1));

        // 2. Datos después del guardado (el 'save' devuelve el objeto sin la lista de sucursales)
        Franchise savedFranchiseShell = new Franchise(franchiseId, newName, List.of());

        // 3. Comportamiento de los Mocks
        when(franchiseRepositoryPort.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchiseShell));
        when(branchRepositoryPort.findByFranchiseId(franchiseId)).thenReturn(Flux.just(branch1));

        // --- ACT ---
        Mono<Franchise> resultMono = updateFranchiseNameUseCase.updateFranchiseName(franchiseId, newName);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectNextMatches(franchise ->
                        franchise.name().equals(newName) &&
                                !franchise.branches().isEmpty() &&
                                franchise.branches().get(0).equals(branch1)
                )
                .verifyComplete();

        verify(franchiseRepositoryPort, times(1)).findById(franchiseId);
        verify(franchiseRepositoryPort, times(1)).save(any(Franchise.class));
        verify(branchRepositoryPort, times(1)).findByFranchiseId(franchiseId);
    }

    @Test
    void updateFranchiseName_shouldReturnException_whenFranchiseNotFound() {
        // --- ARRANGE ---
        UUID franchiseId = UUID.randomUUID();
        String newName = "Nombre que no se usará";

        when(franchiseRepositoryPort.findById(franchiseId)).thenReturn(Mono.empty());

        // --- ACT ---
        Mono<Franchise> resultMono = updateFranchiseNameUseCase.updateFranchiseName(franchiseId, newName);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.FRANCHISE_NOT_FOUND
                )
                .verify();

        verify(franchiseRepositoryPort, never()).save(any(Franchise.class));
        verify(branchRepositoryPort, never()).findByFranchiseId(any(UUID.class));
    }

    @Test
    void updateFranchiseName_shouldReturnError_whenSaveFails() {
        // --- ARRANGE ---
        UUID franchiseId = UUID.randomUUID();
        String newName = "Nombre que fallará";
        Franchise existingFranchise = new Franchise(franchiseId, "Nombre Antiguo", List.of());

        when(franchiseRepositoryPort.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // --- ACT ---
        Mono<Franchise> resultMono = updateFranchiseNameUseCase.updateFranchiseName(franchiseId, newName);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectErrorMessage("Error de base de datos")
                .verify();

        verify(branchRepositoryPort, never()).findByFranchiseId(any(UUID.class));
    }
}
