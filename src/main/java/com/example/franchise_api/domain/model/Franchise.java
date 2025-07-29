package com.example.franchise_api.domain.model;

import java.util.List;
import java.util.UUID;

public record Franchise(
   UUID id,
   String name,
   List<Branch> branches
) {
}
