package com.example.franchise_api.domain.model;

public record Product(
        long id,
        String name,
        int stock
) {
}
