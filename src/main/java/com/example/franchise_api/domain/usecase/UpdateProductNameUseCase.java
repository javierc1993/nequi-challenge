package com.example.franchise_api.domain.usecase;

import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public Mono<Product> updateProductName(UUID productId, String newName) {
        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND, productId)))
                .flatMap(productToUpdate ->
                        productRepositoryPort.findByNameAndBranchIdAndActiveTrue(newName, productToUpdate.branchId())
                                .filter(existingProduct -> !existingProduct.id().equals(productId))
                                .hasElements()
                                .flatMap(nameExists -> {
                                    if (nameExists) {
                                        return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_ALREADY_EXIST, newName));
                                    }
                                    Product updatedProduct = new Product(
                                            productToUpdate.id(),
                                            newName,
                                            productToUpdate.stock(),
                                            productToUpdate.active(),
                                            productToUpdate.branchId()
                                    );
                                    return productRepositoryPort.save(updatedProduct);
                                })
                );
    }
}
