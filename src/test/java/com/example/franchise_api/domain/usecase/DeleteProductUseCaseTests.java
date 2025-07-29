package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteProductUseCaseTests {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private DeleteProductUseCase deleteProductUseCase;

    @Test
    void deleteProduct_shouldDeactivateAndReturnProduct_whenProductExists() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product existingProduct = new Product(productId, "Pizza Activa", 10, true, UUID.randomUUID());
        Product deactivatedProduct = new Product(productId, "Pizza Activa", 10, false, existingProduct.branchId());

        // Simulamos la búsqueda y el guardado exitosos
        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(Mono.just(deactivatedProduct));

        // --- ACT ---
        Mono<Product> resultMono = deleteProductUseCase.deleteProduct(productId);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                // Verificamos que el producto emitido esté inactivo
                .expectNextMatches(product -> !product.active())
                .verifyComplete();

        // Verificación extra y más precisa con ArgumentCaptor
        // Capturamos el objeto que se pasó al método save()
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepositoryPort, times(1)).save(productCaptor.capture());

        // Verificamos que el producto capturado tenga el estado 'active' en false
        assertThat(productCaptor.getValue().active()).isFalse();
    }

    @Test
    void deleteProduct_shouldReturnException_whenProductNotFound() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();

        // Simulamos que el producto no se encuentra (Mono vacío)
        when(productRepositoryPort.findById(productId)).thenReturn(Mono.empty());

        // --- ACT ---
        Mono<Product> resultMono = deleteProductUseCase.deleteProduct(productId);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                // Verificamos que el flujo termine con el error de negocio esperado
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_NOT_FOUND
                )
                .verify();

        // Verificamos que NUNCA se intentó guardar nada
        verify(productRepositoryPort, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_shouldReturnError_whenSaveFails() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product existingProduct = new Product(productId, "Pizza Activa", 10, true, UUID.randomUUID());

        // Simulamos que la búsqueda es exitosa
        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(existingProduct));
        // Pero el guardado falla
        when(productRepositoryPort.save(any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("Error en la base de datos")));

        // --- ACT ---
        Mono<Product> resultMono = deleteProductUseCase.deleteProduct(productId);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                // Verificamos que el error del guardado se propaga
                .expectErrorMessage("Error en la base de datos")
                .verify();
    }
}
