package com.example.franchise_api.infrastructure.adapters.persistenceadapter;

import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.domain.spi.ProductRepositoryPort;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.mapper.ProductPersistenceMapper;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductRepository productR2dbcRepository;
    private final ProductPersistenceMapper productMapper;


    @Override
    public Mono<Product> save(Product product) {
        return Mono.just(productMapper.toEntity(product))
                .flatMap(productR2dbcRepository::save)
                .map(productMapper::toProduct);
    }
}
