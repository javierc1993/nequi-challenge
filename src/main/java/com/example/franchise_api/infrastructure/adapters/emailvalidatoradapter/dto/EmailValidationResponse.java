package com.example.franchise_api.infrastructure.adapters.emailvalidatoradapter.dto;

public record EmailValidationResponse(String deliverability, String quality_score) {
}
