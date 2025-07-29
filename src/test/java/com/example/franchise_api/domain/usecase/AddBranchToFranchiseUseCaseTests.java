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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddBranchToFranchiseUseCaseTests {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @InjectMocks
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;


    @Test
    void addBranch_shouldSaveAndReturnBranch_whenFranchiseExists() {
        // --- ARRANGE (Preparación) ---
        UUID franchiseId = UUID.randomUUID();
        Franchise foundFranchise = new Franchise(franchiseId, "Test Franchise", new ArrayList<>());

        // El objeto Branch que simulamos que llega en la petición
        Branch newBranchRequest = new Branch(null, "New Branch Name", null, null);

        // El objeto Branch que esperamos que se devuelva después de guardarlo
        Branch savedBranch = new Branch(UUID.randomUUID(), "New Branch Name", franchiseId, new ArrayList<>());

        // 1. Simulamos que el puerto de franquicias SÍ encuentra la franquicia
        when(franchiseRepositoryPort.findById(franchiseId)).thenReturn(Mono.just(foundFranchise));
        // 2. Simulamos que el puerto de sucursales guarda y devuelve la sucursal
        when(branchRepositoryPort.save(any(Branch.class))).thenReturn(Mono.just(savedBranch));


        // --- ACT (Actuación) ---
        Mono<Branch> resultMono = addBranchToFranchiseUseCase.addBranch(franchiseId, newBranchRequest);


        // --- ASSERT (Verificación) ---
        StepVerifier.create(resultMono)
                // Verificamos que el Mono emita la sucursal que simulamos como "guardada"
                .expectNext(savedBranch)
                // Verificamos que el flujo se complete exitosamente
                .verifyComplete();

        // Adicionalmente, verificamos que los métodos de nuestros mocks fueron llamados
        verify(franchiseRepositoryPort, times(1)).findById(franchiseId);
        verify(branchRepositoryPort, times(1)).save(any(Branch.class));
    }
    @Test
    void addBranch_shouldReturnBusinessException_whenFranchiseNotFound() {
        // --- ARRANGE (Preparación) ---
        UUID franchiseId = UUID.randomUUID();
        Branch newBranchRequest = new Branch(null, "New Branch Name", null, null);

        // 1. Simulamos que el puerto de franquicias NO encuentra la franquicia (devuelve un Mono vacío)
        when(franchiseRepositoryPort.findById(franchiseId)).thenReturn(Mono.empty());


        // --- ACT (Actuación) ---
        Mono<Branch> resultMono = addBranchToFranchiseUseCase.addBranch(franchiseId, newBranchRequest);


        // --- ASSERT (Verificación) ---
        StepVerifier.create(resultMono)
                // Verificamos que el flujo termine con un error
                .expectErrorMatches(throwable ->
                        // Y que ese error sea de tipo BusinessException con el mensaje correcto
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.FRANCHISE_NOT_FOUND
                )
                // Iniciamos la verificación
                .verify();

        // Verificamos que el método save NUNCA fue llamado, porque el flujo falló antes
        verify(branchRepositoryPort, never()).save(any(Branch.class));
    }

}
