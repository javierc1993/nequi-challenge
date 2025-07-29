package com.example.franchise_api.infrastructure.entrypoints;

import com.example.franchise_api.infrastructure.entrypoints.handler.BranchHandlerImpl;
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
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandlerImpl handler,
                                                          BranchHandlerImpl branchHandler) {
        // Aquí definimos la ruta para crear una franquicia
        return RouterFunctions.route(
                POST("/api/franchises"),
                 handler::createFranchise
        ) .andRoute(
                POST("/api/franchises/{franchiseId}/branches"),
                handler::addBranchToFranchise
        ).andRoute(
                POST("/api/branches/{branchId}/products"),
                branchHandler::addProductToBranch
        );

    }
}
