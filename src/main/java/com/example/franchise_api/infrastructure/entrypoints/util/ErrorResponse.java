package com.example.franchise_api.infrastructure.entrypoints.util;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String path,
        int statusCode,
        LocalDateTime timestamp
) {
}
