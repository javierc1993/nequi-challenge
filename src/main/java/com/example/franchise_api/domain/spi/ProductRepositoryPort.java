package com.example.franchise_api.domain.spi;

import com.example.franchise_api.domain.model.Product;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepositoryPort {
    Mono<Product> save(Product product);
    Mono<Product> findById(UUID id);      // <-- NUEVO MÉTODO
}
