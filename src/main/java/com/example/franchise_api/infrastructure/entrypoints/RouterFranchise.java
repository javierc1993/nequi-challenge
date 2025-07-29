package com.example.franchise_api.infrastructure.entrypoints;

import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.model.Franchise;
import com.example.franchise_api.domain.model.Product;
import com.example.franchise_api.infrastructure.entrypoints.dto.*;
import com.example.franchise_api.infrastructure.entrypoints.handler.BranchHandlerImpl;
import com.example.franchise_api.infrastructure.entrypoints.handler.FranchiseHandlerImpl;
import com.example.franchise_api.infrastructure.entrypoints.handler.ProductHandlerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFranchise {
    @Bean
    @RouterOperations({
            // ====== franchise endpoints ======
            @RouterOperation(
                    path = "/api/franchises", method = RequestMethod.POST, beanClass = FranchiseHandlerImpl.class, beanMethod = "createFranchise",
                    operation = @Operation(operationId = "createFranchise", summary = "create a new franchise", tags = {"Franchises"},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateFranchiseRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "create", content = @Content(schema = @Schema(implementation = Franchise.class))),
                                    @ApiResponse(responseCode = "400", description = "invalid request")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches", method = RequestMethod.POST, beanClass = FranchiseHandlerImpl.class, beanMethod = "addBranchToFranchise",
                    operation = @Operation(operationId = "addBranchToFranchise", summary = "add branch to franchise", tags = {"Franchises", "Branches"},
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "ID de la Franquicia")},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateBranchRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "add ok", content = @Content(schema = @Schema(implementation = Branch.class))),
                                    @ApiResponse(responseCode = "404", description = "franchise not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/name", method = RequestMethod.PATCH, beanClass = FranchiseHandlerImpl.class, beanMethod = "updateFranchiseName",
                    operation = @Operation(operationId = "updateFranchiseName", summary = "update franchise name", tags = {"Franchises"},
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "ID de la Franquicia")},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Updated", content = @Content(schema = @Schema(implementation = Franchise.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found")
                            }
                    )
            ),

            // ====== branches endPoints ======
            @RouterOperation(
                    path = "/api/branches/{branchId}/products", method = RequestMethod.POST, beanClass = BranchHandlerImpl.class, beanMethod = "addProductToBranch",
                    operation = @Operation(operationId = "addProductToBranch", summary = "Add product to branch", tags = {"Branches", "Products"},
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "branchId", description = "branch ID")},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateProductRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(schema = @Schema(implementation = Product.class))),
                                    @ApiResponse(responseCode = "404", description = "branch not Found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/name", method = RequestMethod.PATCH, beanClass = BranchHandlerImpl.class, beanMethod = "updateBranchName",
                    operation = @Operation(operationId = "updateBranchName", summary = "Update branch name ", tags = {"Branches"},
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "branchId", description = "Branch Id")},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "UPDATED", content = @Content(schema = @Schema(implementation = Branch.class))),
                                    @ApiResponse(responseCode = "404", description = "branch not found")
                            }
                    )
            ),

            // ====== products endPoints ======
            @RouterOperation(
                    path = "/api/products/{productId}", method = RequestMethod.DELETE, beanClass = ProductHandlerImpl.class, beanMethod = "deleteProduct",
                    operation = @Operation(operationId = "deleteProduct", summary = "InActivated product", tags = {"Products"},
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "productId", description = "Product ID")},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "DONE", content = @Content(schema = @Schema(implementation = Product.class))),
                                    @ApiResponse(responseCode = "404", description = "Product not Found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/stock", method = RequestMethod.PATCH, beanClass = ProductHandlerImpl.class, beanMethod = "updateStock",
                    operation = @Operation(operationId = "updateStock", summary = "Update product Stock ", tags = {"Products"},
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "productId", description = "product Id")},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "UPDATED", content = @Content(schema = @Schema(implementation = Product.class))),
                                    @ApiResponse(responseCode = "404", description = "Product not Found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/name", method = RequestMethod.PATCH, beanClass = ProductHandlerImpl.class, beanMethod = "updateProductName",
                    operation = @Operation(operationId = "updateProductName", summary = "Update Product Name", tags = {"Products"},
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "productId", description = "product Id")},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "UPDATED", content = @Content(schema = @Schema(implementation = Product.class))),
                                    @ApiResponse(responseCode = "404", description = "Porduct not Found")
                            }
                    )
            ),

            // ====== reports Endpoints======
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/reports/highest-stock-products", method = RequestMethod.GET, beanClass = FranchiseHandlerImpl.class, beanMethod = "getHighestStockReport",
                    operation = @Operation(operationId = "getHighestStockReport", summary = "highest stock product by branch ", tags = {"Reports", "Franchises"},
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "branch Id")},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "report Generated", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "array", implementation = BranchHighestStockReport.class))),
                                    @ApiResponse(responseCode = "404", description = "franchise not found ")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandlerImpl handler, BranchHandlerImpl branchHandler, ProductHandlerImpl productHandler) {

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
        ).andRoute(
                PATCH("/api/branches/{branchId}/name"),
                branchHandler::updateBranchName
        ).andRoute(
                PATCH("/api/products/{productId}/name"),
                productHandler::updateProductName
        );

    }
}
