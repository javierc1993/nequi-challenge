package com.example.franchise_api.domain.model;

import java.util.UUID;

public record Product(
        UUID id,
        String name,
        int stock,
        UUID branchId
) {

    public Product {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
    }
}
