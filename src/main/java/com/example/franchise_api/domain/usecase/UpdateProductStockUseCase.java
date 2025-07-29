package com.example.franchise_api.domain.usecase;
import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public Mono<Product> updateStock(UUID productId, int newStock) {
        // 1. Buscamos el producto existente
        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found with id: " + productId)))
                // 2. Si existe, creamos una copia inmutable con el nuevo stock
                .flatMap(product -> {
                    // La validación de stock negativo se hace en el constructor del record!
                    Product updatedProduct = new Product(
                            product.id(),
                            product.name(),
                            newStock, // <-- El nuevo valor
                            product.active(),
                            product.branchId()
                    );
                    // 3. Guardamos el producto actualizado, lo que resultará en un UPDATE
                    return productRepositoryPort.save(updatedProduct);
                });
    }
}
