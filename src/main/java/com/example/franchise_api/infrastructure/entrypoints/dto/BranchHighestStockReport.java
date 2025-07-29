package com.example.franchise_api.infrastructure.entrypoints.dto;

import com.example.franchise_api.domain.model.Product;

import java.util.UUID;

public record BranchHighestStockReport(
        UUID branchId,
        String branchName,
        Product productWithHighestStock
) {
}
