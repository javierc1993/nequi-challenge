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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private UpdateProductStockUseCase updateProductStockUseCase;

    @Test
    void updateStock_shouldUpdateAndReturnProduct_whenProductExists() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        int newStock = 200;
        Product existingProduct = new Product(productId, "Pizza Original", 50, true, UUID.randomUUID());
        Product updatedProduct = new Product(productId, "Pizza Original", newStock, true, existingProduct.branchId());

        // Simulamos la búsqueda y el guardado exitosos
        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        // --- ACT ---
        Mono<Product> resultMono = updateProductStockUseCase.updateStock(productId, newStock);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectNextMatches(product -> product.stock() == newStock)
                .verifyComplete();

        // Verificación precisa del objeto guardado
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepositoryPort, times(1)).save(productCaptor.capture());
        assertThat(productCaptor.getValue().stock()).isEqualTo(newStock);
    }

    @Test
    void updateStock_shouldReturnException_whenProductNotFound() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        int newStock = 100;

        // Simulamos que el producto no se encuentra
        when(productRepositoryPort.findById(productId)).thenReturn(Mono.empty());

        // --- ACT ---
        Mono<Product> resultMono = updateProductStockUseCase.updateStock(productId, newStock);

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
    void updateStock_shouldReturnError_whenSaveFails() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        int newStock = 150;
        Product existingProduct = new Product(productId, "Pizza a fallar", 50, true, UUID.randomUUID());

        // Simulamos que la búsqueda es exitosa
        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(existingProduct));
        // Pero el guardado falla
        when(productRepositoryPort.save(any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("Error de escritura en BD")));

        // --- ACT ---
        Mono<Product> resultMono = updateProductStockUseCase.updateStock(productId, newStock);

        // --- ASSERT ---
        StepVerifier.create(resultMono)
                .expectErrorMessage("Error de escritura en BD")
                .verify();
    }
}
