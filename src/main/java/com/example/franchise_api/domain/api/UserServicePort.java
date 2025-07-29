package com.example.franchise_api.domain.api;

import com.example.franchise_api.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserServicePort {
    Mono<User> registerUser(User user, String messageId);
}
