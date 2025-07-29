package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class AddProductToBranchUseCase {
    private final BranchRepositoryPort branchRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public Mono<Product> addProduct(UUID branchId, Product newProduct) {
        // 1. Validamos que la sucursal exista
        return branchRepositoryPort.findById(branchId)
                .switchIfEmpty(Mono.error(new RuntimeException("Branch not found with id: " + branchId)))
                // 2. Si existe, creamos el producto y lo guardamos
                .flatMap(branch -> {
                    Product productToSave = new Product(
                            null, // ID generado por la BD
                            newProduct.name(),
                            newProduct.stock(),
                            true,
                            branch.id() // Asignamos el ID de la sucursal padre
                    );
                    return productRepositoryPort.save(productToSave);
                });
    }

}
