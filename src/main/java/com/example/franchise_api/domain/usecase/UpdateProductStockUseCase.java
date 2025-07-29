package com.example.franchise_api.domain.usecase;
import com.example.franchise_api.domain.enums.TechnicalMessage;
import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public Mono<Product> updateStock(UUID productId, int newStock) {
        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND,productId)))
                .flatMap(product -> {
                    Product updatedProduct = new Product(
                            product.id(),
                            product.name(),
                            newStock,
                            product.active(),
                            product.branchId()
                    );
                    return productRepositoryPort.save(updatedProduct);
                });
    }
}
