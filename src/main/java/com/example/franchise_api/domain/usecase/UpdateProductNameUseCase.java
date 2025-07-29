package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public Mono<Product> updateProductName(UUID productId, String newName) {
        // 1. Buscamos el producto que se quiere actualizar.
        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found with id: " + productId)))
                // 2. Si existe, validamos que el nuevo nombre no esté ya en uso en esa sucursal.
                .flatMap(productToUpdate ->
                        productRepositoryPort.findByNameAndBranchIdAndActiveTrue(newName, productToUpdate.branchId())
                                // Filtramos para ver si existe OTRO producto con ese nombre.
                                .filter(existingProduct -> !existingProduct.id().equals(productId))
                                .hasElements()
                                .flatMap(nameExists -> {
                                    if (nameExists) {
                                        return Mono.error(new RuntimeException("Product name '" + newName + "' already exists in this branch."));
                                    }
                                    // 3. Si el nombre es válido, creamos la copia actualizada.
                                    Product updatedProduct = new Product(
                                            productToUpdate.id(),
                                            newName,
                                            productToUpdate.stock(),
                                            productToUpdate.active(),
                                            productToUpdate.branchId()
                                    );
                                    // 4. Guardamos el producto.
                                    return productRepositoryPort.save(updatedProduct);
                                })
                );
    }
}
