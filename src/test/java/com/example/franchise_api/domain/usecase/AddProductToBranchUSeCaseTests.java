package com.example.franchise_api.domain.usecase;
import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddProductToBranchUSeCaseTests {

    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private AddProductToBranchUseCase addProductToBranchUseCase;

    @Test
    void addProduct_shouldSaveProduct_whenBranchExistsAndProductIsNew() {
        // --- ARRANGE ---
        UUID branchId = UUID.randomUUID();
        UUID franchiseId = UUID.randomUUID();

        Product newProduct = new Product(null, "New Pizza", 50, true, null);
        Branch foundBranch = new Branch(branchId, "Test Branch", franchiseId, new ArrayList<>());
        Product savedProduct = new Product(UUID.randomUUID(), "New Pizza", 50, true, branchId);

        // 1. Simulamos que el producto NO existe (el Flux viene vacío)
        when(productRepositoryPort.findByNameAndBranchIdAndActiveTrue(newProduct.name(), branchId))
                .thenReturn(Flux.empty());
        // 2. Simulamos que la sucursal SÍ existe
        when(branchRepositoryPort.findById(branchId)).thenReturn(Mono.just(foundBranch));
        // 3. Simulamos la operación de guardado exitosa
        when(productRepositoryPort.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        // --- ACT ---
        Mono<Product> resultMono = addProductToBranchUseCase.addProduct(branchId, newProduct);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectNext(savedProduct)
                .verifyComplete();

        verify(productRepositoryPort, times(1)).save(any(Product.class));
    }

    @Test
    void addProduct_shouldThrowException_whenProductAlreadyExists() {
        // --- ARRANGE ---
        UUID branchId = UUID.randomUUID();
        Product newProduct = new Product(null, "Existing Pizza", 50, true, null);
        Product existingProduct = new Product(UUID.randomUUID(), "Existing Pizza", 30, true, branchId);

        // 1. Simulamos que el producto SÍ existe (el Flux emite un elemento)
        when(productRepositoryPort.findByNameAndBranchIdAndActiveTrue(newProduct.name(), branchId))
                .thenReturn(Flux.just(existingProduct));

        // --- ACT ---
        Mono<Product> resultMono = addProductToBranchUseCase.addProduct(branchId, newProduct);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                // Verificamos que el flujo termine con el error de negocio esperado
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_ALREADY_EXIST
                )
                .verify();

        // Verificamos que el flujo se detuvo y nunca intentó buscar o guardar nada más
        verify(branchRepositoryPort, never()).findById(any(UUID.class));
        verify(productRepositoryPort, never()).save(any(Product.class));
    }

    @Test
    void addProduct_shouldThrowException_whenBranchNotFound() {
        // --- ARRANGE ---
        UUID branchId = UUID.randomUUID();
        Product newProduct = new Product(null, "New Pizza", 50, true, null);

        // 1. Simulamos que el producto NO existe
        when(productRepositoryPort.findByNameAndBranchIdAndActiveTrue(newProduct.name(), branchId))
                .thenReturn(Flux.empty());
        // 2. Simulamos que la sucursal NO existe
        when(branchRepositoryPort.findById(branchId)).thenReturn(Mono.empty());

        // --- ACT ---
        Mono<Product> resultMono = addProductToBranchUseCase.addProduct(branchId, newProduct);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                // Verificamos que el flujo termine con el error de negocio esperado
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.BRANCH_NOT_FOUND
                )
                .verify();

        // Verificamos que el flujo se detuvo y nunca intentó guardar el producto
        verify(productRepositoryPort, never()).save(any(Product.class));
    }
}
