package com.example.franchise_api.infrastructure.entrypoints.handler;

import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.infrastructure.entrypoints.dto.CreateProductRequest;
import com.example.franchise_api.infrastructure.entrypoints.mapper.ProductRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import com.example.franchise_api.domain.usecase.AddProductToBranchUseCase;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BranchHandlerImpl {

    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final ProductRestMapper productRestMapper;


    public Mono<ServerResponse> addProductToBranch(ServerRequest request) {
        UUID branchId = UUID.fromString(request.pathVariable("branchId"));

        return request.bodyToMono(CreateProductRequest.class)
                .map(productRestMapper::toProduct)// Mapeo simple
                .flatMap(product -> addProductToBranchUseCase.addProduct(branchId, product))
                .flatMap(savedProduct -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedProduct))
                .onErrorResume(RuntimeException.class, e -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

}
