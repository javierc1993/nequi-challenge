package com.example.franchise_api.domain.spi;

import com.example.franchise_api.domain.model.EmailValidationResult;
import reactor.core.publisher.Mono;

public interface EmailValidatorGateway {

    Mono<EmailValidationResult> validateEmail(String email, String messageId);
}
