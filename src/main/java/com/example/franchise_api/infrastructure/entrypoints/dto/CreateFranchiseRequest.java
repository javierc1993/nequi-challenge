package com.example.franchise_api.infrastructure.entrypoints.dto;

import java.util.List;

public record CreateFranchiseRequest(
        String name,
        List<CreateBranchRequest> branches
) {
}
