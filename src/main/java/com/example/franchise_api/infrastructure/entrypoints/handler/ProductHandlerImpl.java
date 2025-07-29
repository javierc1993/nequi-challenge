package com.example.franchise_api.infrastructure.entrypoints.handler;


import com.example.franchise_api.domain.usecase.DeleteProductUseCase;
import com.example.franchise_api.domain.usecase.UpdateProductNameUseCase;
import com.example.franchise_api.domain.usecase.UpdateProductStockUseCase;
import com.example.franchise_api.infrastructure.entrypoints.dto.UpdateNameRequest;
import com.example.franchise_api.infrastructure.entrypoints.dto.UpdateStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static com.example.franchise_api.infrastructure.entrypoints.util.Constants.ERROR;
import static com.example.franchise_api.infrastructure.entrypoints.util.Constants.ALREADY_EXISTS;
import static com.example.franchise_api.infrastructure.entrypoints.util.Constants.CANNOT_EMPTY;

@Component
@RequiredArgsConstructor
public class ProductHandlerImpl {
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        UUID productId = UUID.fromString(request.pathVariable("productId"));

        return deleteProductUseCase.deleteProduct(productId)
                .flatMap(deactivatedProduct -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(deactivatedProduct))
                .onErrorResume(RuntimeException.class, e -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(Map.of(ERROR, e.getMessage())));

    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        UUID productId = UUID.fromString(request.pathVariable("productId"));

        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(updateRequest -> updateProductStockUseCase.updateStock(productId, updateRequest.stock()))
                .flatMap(updatedProduct -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedProduct))
                .onErrorResume(RuntimeException.class, e -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(Map.of(ERROR, e.getMessage())))
                .onErrorResume(IllegalArgumentException.class, e -> ServerResponse
                        .badRequest()
                        .bodyValue(Map.of(ERROR, e.getMessage())));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        UUID productId = UUID.fromString(request.pathVariable("productId"));

        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(updateRequest -> updateProductNameUseCase.updateProductName(productId, updateRequest.name()))
                .flatMap(updatedProduct -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedProduct));
    }
}
