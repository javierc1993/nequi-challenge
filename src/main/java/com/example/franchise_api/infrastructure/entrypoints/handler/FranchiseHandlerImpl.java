package com.example.franchise_api.infrastructure.entrypoints.handler;


import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.usecase.AddBranchToFranchiseUseCase;
import com.example.franchise_api.domain.usecase.CreateFranchiseUseCase;
import com.example.franchise_api.domain.usecase.GetHighestStockProductReportUseCase;
import com.example.franchise_api.domain.usecase.UpdateFranchiseNameUseCase;
import com.example.franchise_api.infrastructure.entrypoints.dto.*;
import com.example.franchise_api.infrastructure.entrypoints.mapper.FranchiseRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static com.example.franchise_api.infrastructure.entrypoints.util.Constants.ERROR;

@Component
@RequiredArgsConstructor
public class FranchiseHandlerImpl {
    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final FranchiseRestMapper franchiseRestMapper;
    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    private final GetHighestStockProductReportUseCase getHighestStockProductReportUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    /**
     * Maneja la petición POST para crear una nueva franquicia.
     * @param request La petición entrante del router.
     * @return Una respuesta reactiva (Mono<ServerResponse>).
     */
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequest.class)
                .map(franchiseRestMapper::toFranchise)
                .flatMap(createFranchiseUseCase::create)
                .flatMap(savedFranchise -> ServerResponse
                        .status(HttpStatus.CREATED) // 4a. Si todo va bien, prepara una respuesta 201 CREATED.
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedFranchise))
                .onErrorResume(IllegalArgumentException.class, e -> ServerResponse
                        .status(HttpStatus.BAD_REQUEST) // HTTP 400
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(ERROR, e.getMessage())))
                .onErrorResume(Exception.class, e -> {
                    // ===== INICIO DEL CÓDIGO AÑADIDO PARA DEBUG =====
                    System.err.println("************************************************************");
                    System.err.println("¡ERROR INESPERADO ATRAPADO EN EL HANDLER!");
                    System.err.println("Tipo de Excepción: " + e.getClass().getName());
                    System.err.println("Mensaje: " + e.getMessage());
                    e.printStackTrace(); // ¡Esta es la línea que nos mostrará el error real!
                    System.err.println("************************************************************");
                    // ===== FIN DEL CÓDIGO AÑADIDO PARA DEBUG =====

                    return ServerResponse
                            .status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of(ERROR, "An unexpected error occurred."));
                });
    }

    public Mono<ServerResponse> addBranchToFranchise(ServerRequest request) {
        UUID franchiseId = UUID.fromString(request.pathVariable("franchiseId"));

        Mono<Branch> branchMono = request.bodyToMono(CreateBranchRequest.class)
                .map(franchiseRestMapper::toBranch);
        return branchMono
                .flatMap(branch -> addBranchToFranchiseUseCase.addBranch(franchiseId, branch))
                .flatMap(savedBranch -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedBranch))
                .onErrorResume(RuntimeException.class, e -> ServerResponse
                        .status(HttpStatus.NOT_FOUND) // HTTP 404
                        .bodyValue(Map.of(ERROR, e.getMessage())));
    }

    public Mono<ServerResponse> getHighestStockReport(ServerRequest request) {
        UUID franchiseId = UUID.fromString(request.pathVariable("franchiseId"));

        Flux<BranchHighestStockReport> reportFlux = getHighestStockProductReportUseCase.getReport(franchiseId);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reportFlux, BranchHighestStockReport.class);
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        UUID franchiseId = UUID.fromString(request.pathVariable("franchiseId"));

        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(updateRequest -> updateFranchiseNameUseCase.updateFranchiseName(franchiseId, updateRequest.name()))
                .flatMap(updatedFranchise -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedFranchise))
                .onErrorResume(RuntimeException.class, e -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(Map.of(ERROR, e.getMessage())))
                .onErrorResume(IllegalArgumentException.class, e -> ServerResponse
                        .badRequest()
                        .bodyValue(Map.of(ERROR, e.getMessage())));
    }


}
