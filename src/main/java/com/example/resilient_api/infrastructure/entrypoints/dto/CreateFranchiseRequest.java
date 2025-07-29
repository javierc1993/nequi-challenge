package com.example.resilient_api.infrastructure.entrypoints.dto;

import java.util.List;

public record CreateFranchiseRequest(
        String name,
        List<CreateBranchRequest> branches
) {
}
