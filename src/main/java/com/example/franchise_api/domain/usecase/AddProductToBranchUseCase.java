package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
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
        return productRepositoryPort.findByNameAndBranchIdAndActiveTrue(newProduct.name(), branchId)
                .hasElements()
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_ALREADY_EXIST, "")))
                .flatMap(exists -> branchRepositoryPort.findById(branchId))
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND, branchId)))
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
