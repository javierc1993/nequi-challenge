package com.example.franchise_api.application.config;

import com.example.franchise_api.domain.spi.BranchRepositoryPort;
import com.example.franchise_api.domain.spi.EmailValidatorGateway;
import com.example.franchise_api.domain.spi.FranchiseRepositoryPort;
import com.example.franchise_api.domain.spi.UserPersistencePort;
import com.example.franchise_api.domain.usecase.CreateFranchiseUseCase;
import com.example.franchise_api.domain.usecase.UserUseCase;
import com.example.franchise_api.domain.api.UserServicePort;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.UserPersistenceAdapter;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.mapper.UserEntityMapper;
import com.example.franchise_api.infrastructure.adapters.persistenceadapter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final UserRepository userRepository;
        private final UserEntityMapper userEntityMapper;

        @Bean
        public UserPersistencePort usersPersistencePort() {
                return new UserPersistenceAdapter(userRepository,userEntityMapper);
        }

        @Bean
        public UserServicePort usersServicePort(UserPersistencePort usersPersistencePort, EmailValidatorGateway emailValidatorGateway){
                return new UserUseCase(usersPersistencePort, emailValidatorGateway);
        }

        @Bean // <-- ¡Esta es la anotación clave!
        public CreateFranchiseUseCase createFranchiseUseCase(
                FranchiseRepositoryPort franchiseRepositoryPort,
                BranchRepositoryPort branchRepositoryPort) {

                // Creamos la instancia del caso de uso, inyectándole los puertos que necesita.
                // Spring se encargará de pasarle las implementaciones correctas (los Adapters).
                return new CreateFranchiseUseCase(franchiseRepositoryPort, branchRepositoryPort);
        }
}
