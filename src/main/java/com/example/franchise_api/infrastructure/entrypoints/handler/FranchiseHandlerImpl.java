package com.example.franchise_api.infrastructure.entrypoints.handler;


import com.example.franchise_api.domain.model.Branch;
import com.example.franchise_api.domain.usecase.AddBranchToFranchiseUseCase;
import com.example.franchise_api.domain.usecase.CreateFranchiseUseCase;
import com.example.franchise_api.domain.usecase.GetHighestStockProductReportUseCase;
import com.example.franchise_api.infrastructure.entrypoints.dto.BranchHighestStockReport;
import com.example.franchise_api.infrastructure.entrypoints.dto.CreateBranchRequest;
import com.example.franchise_api.infrastructure.entrypoints.dto.CreateFranchiseRequest;
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

@Component
@RequiredArgsConstructor
public class FranchiseHandlerImpl {
    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final FranchiseRestMapper franchiseRestMapper;
    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    private final GetHighestStockProductReportUseCase getHighestStockProductReportUseCase;

    /**
     * Maneja la petición POST para crear una nueva franquicia.
     * @param request La petición entrante del router.
     * @return Una respuesta reactiva (Mono<ServerResponse>).
     */
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequest.class)
                // 1. Deserializa el JSON del body a nuestro DTO.
                .map(franchiseRestMapper::toFranchise)
                // 2. Mapea el DTO a nuestro modelo de Dominio.
                .flatMap(createFranchiseUseCase::create)
                // 3. Delega la lógica de negocio al Caso de Uso.
                .flatMap(savedFranchise -> ServerResponse
                        .status(HttpStatus.CREATED) // 4a. Si todo va bien, prepara una respuesta 201 CREATED.
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedFranchise))
                // 4b. Manejo de errores controlados (ej: validación del dominio).
                .onErrorResume(IllegalArgumentException.class, e -> ServerResponse
                        .status(HttpStatus.BAD_REQUEST) // HTTP 400
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())))
                // 4c. Manejo de errores inesperados.
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
                            .bodyValue(Map.of("error", "An unexpected error occurred."));
                });
    }

    public Mono<ServerResponse> addBranchToFranchise(ServerRequest request) {
        // Extraemos el 'franchiseId' de la URL
        UUID franchiseId = UUID.fromString(request.pathVariable("franchiseId"));

        // Creamos un Mono para el objeto Branch a partir del body
        Mono<Branch> branchMono = request.bodyToMono(CreateBranchRequest.class)
                .map(franchiseRestMapper::toBranch); // Necesitarás un toBranch en el mapper

        // Combinamos la información del path y del body para llamar al UseCase
        return branchMono
                .flatMap(branch -> addBranchToFranchiseUseCase.addBranch(franchiseId, branch))
                .flatMap(savedBranch -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedBranch))
                // Manejamos el error "Franchise not found" que definimos en el UseCase
                .onErrorResume(RuntimeException.class, e -> ServerResponse
                        .status(HttpStatus.NOT_FOUND) // HTTP 404
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getHighestStockReport(ServerRequest request) {
        UUID franchiseId = UUID.fromString(request.pathVariable("franchiseId"));

        // El UseCase ya devuelve un Flux con el DTO que necesitamos.
        Flux<BranchHighestStockReport> reportFlux = getHighestStockProductReportUseCase.getReport(franchiseId);

        // Simplemente pasamos ese Flux al cuerpo de la respuesta.
        // Spring se encargará de recolectarlo en un arreglo JSON.
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reportFlux, BranchHighestStockReport.class);
    }


}
