package com.example.franchise_api.infrastructure.entrypoints;

import com.example.franchise_api.infrastructure.entrypoints.handler.BranchHandlerImpl;
import com.example.franchise_api.infrastructure.entrypoints.handler.FranchiseHandlerImpl;
import com.example.franchise_api.infrastructure.entrypoints.handler.ProductHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFranchise {
    @Bean
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandlerImpl handler, BranchHandlerImpl branchHandler, ProductHandlerImpl productHandler) {
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
        ).andRoute(
                DELETE("/api/products/{productId}"),
                productHandler::deleteProduct
        ).andRoute(
                PATCH("/api/products/{productId}/stock"),
                productHandler::updateStock
        ).andRoute(
                GET("/api/franchises/{franchiseId}/reports/highest-stock-products"),
                handler::getHighestStockReport
        ).andRoute(
                PATCH("/api/franchises/{franchiseId}/name"),
                handler::updateFranchiseName
        );

    }
}
