package com.example.resilient_api.domain.model;

import java.util.List;
import java.util.UUID;

public record Branch(
        UUID id,
        String name,
        UUID franchiseId,
        List<Product> products
) {
}
