package com.example.franchise_api.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INTERNAL_ERROR_IN_ADAPTERS("PRC501","Something went wrong in adapters, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    UNSUPPORTED_OPERATION("501", "Method not supported, please try again", ""),
    FRANCHISE_NOT_FOUND("404", "franchise not found with id: %s", ""),
    BRANCH_NOT_FOUND("404", "Branch not found with id: %s", ""),
    PRODUCT_NOT_FOUND("404", "Product not found with id: %s", ""),
    PRODUCT_ALREADY_EXIST("400","The product already exist : %s" ,"" );
    private final String code;
    private final String message;
    private final String param;
}