package com.example.franchise_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "Franchise Management API",
		version = "1.0.0",
		description = "API to franchise, branches and products gestion"
))
public class FranchiseApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FranchiseApiApplication.class, args);
	}

}
