package com.example.franchise_api.domain.spi;

import com.example.franchise_api.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {
    Mono<Product> save(Product product);
}
