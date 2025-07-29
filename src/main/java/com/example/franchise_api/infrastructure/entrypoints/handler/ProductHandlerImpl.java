package com.example.franchise_api.infrastructure.entrypoints.handler;


import com.example.franchise_api.domain.usecase.DeleteProductUseCase;
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
}
