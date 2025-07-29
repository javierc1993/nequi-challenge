package com.example.franchise_api.application.config;

import com.example.franchise_api.domain.spi.*;
import com.example.franchise_api.domain.usecase.*;
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

        @Bean
        public CreateFranchiseUseCase createFranchiseUseCase(
                FranchiseRepositoryPort franchiseRepositoryPort,
                BranchRepositoryPort branchRepositoryPort) {

                return new CreateFranchiseUseCase(franchiseRepositoryPort, branchRepositoryPort);
        }

        @Bean
        public AddBranchToFranchiseUseCase addBranchToFranchiseUseCase(
                FranchiseRepositoryPort franchiseRepositoryPort,
                BranchRepositoryPort branchRepositoryPort) {

                return new AddBranchToFranchiseUseCase(franchiseRepositoryPort, branchRepositoryPort);
        }

        @Bean
        public AddProductToBranchUseCase addProductToBranchUseCase(
                ProductRepositoryPort productRepositoryPort,
                BranchRepositoryPort branchRepositoryPort) {

                return new AddProductToBranchUseCase(branchRepositoryPort,productRepositoryPort );
        }

        @Bean
        public DeleteProductUseCase deleteProductUseCase(
                ProductRepositoryPort productRepositoryPort) {
                return new DeleteProductUseCase(productRepositoryPort );
        }

        @Bean
        public UpdateProductStockUseCase updateProductStockUseCase(
                ProductRepositoryPort productRepositoryPort) {
                return new UpdateProductStockUseCase(productRepositoryPort );
        }

        @Bean
        public GetHighestStockProductReportUseCase getHighestStockProductReportUseCase(
                FranchiseRepositoryPort franchiseRepositoryPort,
                BranchRepositoryPort branchRepositoryPort,
                ProductRepositoryPort productRepositoryPort) {
                return new GetHighestStockProductReportUseCase(franchiseRepositoryPort,branchRepositoryPort,productRepositoryPort );
        }

        @Bean
        public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(
                FranchiseRepositoryPort franchiseRepositoryPort,
                BranchRepositoryPort branchRepositoryPort) {
                return new UpdateFranchiseNameUseCase(franchiseRepositoryPort,branchRepositoryPort );
        }

        @Bean
        public UpdateBranchNameUsecase updateBranchNameUseCase(
                BranchRepositoryPort branchRepositoryPort) {
                return new UpdateBranchNameUsecase(branchRepositoryPort );
        }

        @Bean
        public UpdateProductNameUseCase updateProductNameUsecase(
                ProductRepositoryPort productRepositoryPort) {
                return new UpdateProductNameUseCase(productRepositoryPort);
        }




}
