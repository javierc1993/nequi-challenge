package com.example.franchise_api.infrastructure.entrypoints.handler;

import com.example.franchise_api.domain.exceptions.BusinessException;
import com.example.franchise_api.domain.exceptions.TechnicalException;
import com.example.franchise_api.infrastructure.entrypoints.util.ErrorResponse;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                                          ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        // Obtenemos el error original
        Throwable error = getError(request);
        HttpStatus httpStatus;
        String message;

        // Ahora podemos hacer un 'instanceof' para cada tipo de error
        if (error instanceof BusinessException) {
            // Si es un error de negocio, decidimos el status.
            // Podríamos ser más específicos si BusinessException tuviera un campo de código de error.
            if (error.getMessage().contains("not found")) {
                httpStatus = HttpStatus.NOT_FOUND; // 404
            } else {
                httpStatus = HttpStatus.BAD_REQUEST; // 400 por defecto para otros errores de negocio
            }
            message = error.getMessage(); // El mensaje es seguro para el cliente.
        } else if (error instanceof TechnicalException) {
            // Si es un error técnico, siempre es un 5xx y NUNCA filtramos el mensaje.
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 500
            message = "A technical error occurred, please try again later."; // Mensaje genérico
        } else {
            // Red de seguridad para cualquier otra excepción inesperada
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 500
            message = "An unexpected server error occurred.";
        }

        // Creamos nuestro DTO de respuesta de error personalizado
        ErrorResponse errorResponse = new ErrorResponse(
                message, // Mensaje de nuestra excepción
                request.path(),
                httpStatus.value(),
                LocalDateTime.now()
        );

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }
}
