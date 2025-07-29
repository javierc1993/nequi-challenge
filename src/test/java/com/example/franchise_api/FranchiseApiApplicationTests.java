package com.example.franchise_api;

import com.example.franchise_api.domain.spi.UserPersistencePort;
import com.example.franchise_api.domain.usecase.UserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = FranchiseApiApplication.class)
class FranchiseApiApplicationTests {

	@MockBean
	private UserPersistencePort userPersistencePort;

	@Autowired
	private UserUseCase userUseCase;

	@Test
	void contextLoads() {
	}

}
