package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class DeleteProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    public Mono<Product> deleteProduct(UUID productId) {
        // 1. Buscamos el producto
        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found with id: " + productId)))
                // 2. Si lo encontramos, creamos una nueva versión con active = false
                .flatMap(product -> {
                    Product deactivatedProduct = new Product(
                            product.id(),
                            product.name(),
                            product.stock(),
                            false,
                            product.branchId()
                    );
                    // 3. Guardamos el producto actualizado. Esto ejecutará un UPDATE.
                    return productRepositoryPort.save(deactivatedProduct);
                });
    }

}
