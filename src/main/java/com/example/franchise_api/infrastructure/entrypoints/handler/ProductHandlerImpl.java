package com.example.franchise_api.infrastructure.entrypoints.handler;


import com.example.franchise_api.domain.usecase.DeleteProductUseCase;
import com.example.franchise_api.domain.usecase.UpdateProductStockUseCase;
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

@Component
@RequiredArgsConstructor
public class ProductHandlerImpl {
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        UUID productId = UUID.fromString(request.pathVariable("productId"));

        return deleteProductUseCase.deleteProduct(productId)
                .flatMap(deactivatedProduct -> ServerResponse.ok() // Devuelve un 200 OK
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(deactivatedProduct))
                .onErrorResume(RuntimeException.class, e -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(Map.of("error", e.getMessage())));

    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        UUID productId = UUID.fromString(request.pathVariable("productId"));

        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(updateRequest -> updateProductStockUseCase.updateStock(productId, updateRequest.stock()))
                .flatMap(updatedProduct -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedProduct))
                // Manejamos el error de producto no encontrado
                .onErrorResume(RuntimeException.class, e -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(Map.of("error", e.getMessage())))
                // Manejamos el error de validación del dominio (stock negativo)
                .onErrorResume(IllegalArgumentException.class, e -> ServerResponse
                        .badRequest() // 400 Bad Request
                        .bodyValue(Map.of("error", e.getMessage())));
    }
}
