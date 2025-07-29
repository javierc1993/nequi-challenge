package com.example.franchise_api.domain.usecase;
import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateBranchNameUseCaseTests {
    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @InjectMocks
    private UpdateBranchNameUsecase updateBranchNameUsecase;

    @Test
    void updateBranchName_shouldUpdateAndReturnBranch_whenBranchExists() {
        // --- ARRANGE ---
        UUID branchId = UUID.randomUUID();
        String newName = "Sucursal Remodelada";
        Branch existingBranch = new Branch(branchId, "Nombre Antiguo", UUID.randomUUID(), new ArrayList<>());
        Branch updatedBranch = new Branch(branchId, newName, existingBranch.franchiseId(), existingBranch.products());

        // Simulamos la búsqueda y el guardado exitosos
        when(branchRepositoryPort.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(branchRepositoryPort.save(any(Branch.class))).thenReturn(Mono.just(updatedBranch));

        // --- ACT ---
        Mono<Branch> resultMono = updateBranchNameUsecase.updateBranchName(branchId, newName);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                // Verificamos que la sucursal emitida tenga el nuevo nombre
                .expectNextMatches(branch -> branch.name().equals(newName))
                .verifyComplete();

        // Usamos ArgumentCaptor para verificar que el objeto que se intentó guardar era el correcto
        ArgumentCaptor<Branch> branchCaptor = ArgumentCaptor.forClass(Branch.class);
        verify(branchRepositoryPort, times(1)).save(branchCaptor.capture());
        assertThat(branchCaptor.getValue().name()).isEqualTo(newName);
        assertThat(branchCaptor.getValue().id()).isEqualTo(branchId);
    }

    @Test
    void updateBranchName_shouldReturnException_whenBranchNotFound() {
        // --- ARRANGE ---
        UUID branchId = UUID.randomUUID();
        String newName = "Nombre Inexistente";

        // Simulamos que la sucursal no se encuentra
        when(branchRepositoryPort.findById(branchId)).thenReturn(Mono.empty());

        // --- ACT ---
        Mono<Branch> resultMono = updateBranchNameUsecase.updateBranchName(branchId, newName);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                // Verificamos que el flujo termine con el error de negocio esperado
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.BRANCH_NOT_FOUND
                )
                .verify();

        // Verificamos que NUNCA se intentó guardar nada
        verify(branchRepositoryPort, never()).save(any(Branch.class));
    }

    @Test
    void updateBranchName_shouldReturnError_whenSaveFails() {
        // --- ARRANGE ---
        UUID branchId = UUID.randomUUID();
        String newName = "Sucursal con fallo";
        Branch existingBranch = new Branch(branchId, "Nombre Antiguo", UUID.randomUUID(), new ArrayList<>());

        // Simulamos que la búsqueda es exitosa
        when(branchRepositoryPort.findById(branchId)).thenReturn(Mono.just(existingBranch));
        // Pero el guardado falla
        when(branchRepositoryPort.save(any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("Error en la base de datos")));

        // --- ACT ---
        Mono<Branch> resultMono = updateBranchNameUsecase.updateBranchName(branchId, newName);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectErrorMessage("Error en la base de datos")
                .verify();
    }
}
