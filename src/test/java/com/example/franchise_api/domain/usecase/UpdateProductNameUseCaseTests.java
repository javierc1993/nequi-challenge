package com.example.franchise_api.domain.usecase;


import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductNameUseCaseTests {
    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private UpdateProductNameUseCase updateProductNameUseCase;

    @Test
    void updateProductName_shouldUpdateAndReturnProduct_whenNameIsUnique() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        String newName = "Nuevo Nombre";
        Product productToUpdate = new Product(productId, "Nombre Antiguo", 10, true, branchId);
        Product expectedUpdatedProduct = new Product(productId, newName, 10, true, branchId);

        // 1. Simulamos que el producto a actualizar SÍ se encuentra
        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(productToUpdate));
        // 2. Simulamos que la búsqueda por el nuevo nombre NO encuentra otros productos
        when(productRepositoryPort.findByNameAndBranchIdAndActiveTrue(newName, branchId)).thenReturn(Flux.empty());
        // 3. Simulamos el guardado exitoso
        when(productRepositoryPort.save(any(Product.class))).thenReturn(Mono.just(expectedUpdatedProduct));

        // --- ACT ---
        Mono<Product> resultMono = updateProductNameUseCase.updateProductName(productId, newName);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectNextMatches(product -> product.name().equals(newName))
                .verifyComplete();

        verify(productRepositoryPort, times(1)).save(any(Product.class));
    }

    @Test
    void updateProductName_shouldReturnException_whenProductNotFound() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        String newName = "No importa";

        // Simulamos que el producto a actualizar NO se encuentra
        when(productRepositoryPort.findById(productId)).thenReturn(Mono.empty());

        // --- ACT ---
        Mono<Product> resultMono = updateProductNameUseCase.updateProductName(productId, newName);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_NOT_FOUND
                )
                .verify();

        verify(productRepositoryPort, never()).findByNameAndBranchIdAndActiveTrue(anyString(), any(UUID.class));
        verify(productRepositoryPort, never()).save(any(Product.class));
    }

    @Test
    void updateProductName_shouldReturnException_whenNewNameAlreadyExists() {
        // --- ARRANGE ---
        UUID productIdToUpdate = UUID.randomUUID();
        UUID anotherProductId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        String newName = "Nombre Duplicado";

        Product productToUpdate = new Product(productIdToUpdate, "Nombre Antiguo", 10, true, branchId);
        Product existingProductWithSameName = new Product(anotherProductId, newName, 20, true, branchId);

        // 1. Simulamos que el producto a actualizar SÍ se encuentra
        when(productRepositoryPort.findById(productIdToUpdate)).thenReturn(Mono.just(productToUpdate));
        // 2. Simulamos que la búsqueda por el nuevo nombre SÍ encuentra OTRO producto
        when(productRepositoryPort.findByNameAndBranchIdAndActiveTrue(newName, branchId))
                .thenReturn(Flux.just(existingProductWithSameName));

        // --- ACT ---
        Mono<Product> resultMono = updateProductNameUseCase.updateProductName(productIdToUpdate, newName);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_ALREADY_EXIST
                )
                .verify();

        verify(productRepositoryPort, never()).save(any(Product.class));
    }
}
