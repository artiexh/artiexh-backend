package com.artiexh.authorization.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.artiexh")
@EntityScan(basePackages = "com.artiexh.data.jpa.entity")
@EnableJpaRepositories(basePackages = "com.artiexh.data.jpa.repository")
public class AuthorizationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationClientApplication.class, args);
	}

}
