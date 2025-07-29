package com.example.franchise_api.infrastructure.entrypoints.dto;

public record CreateProductRequest(
        String name,
        int stock
) {
}
