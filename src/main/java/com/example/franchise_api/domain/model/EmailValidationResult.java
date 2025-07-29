package com.example.franchise_api.domain.model;

public record EmailValidationResult(String deliverability, String quality_score) { }

