package com.example.franchise_api.infrastructure.entrypoints;

import com.example.franchise_api.infrastructure.entrypoints.handler.FranchiseHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class RouterFranchise {
    @Bean
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandlerImpl handler) {
        // Aquí definimos la ruta para crear una franquicia
        return RouterFunctions.route(
                POST("/api/franchises"), // La ruta y el método HTTP
                 handler::createFranchise  // La referencia al método en nuestro handler
        );
    }
}
