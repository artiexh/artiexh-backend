package com.artiexh.api.base.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI openAPI(Environment environment) {
        return new OpenAPI().info(new Info().title(environment.getProperty("spring.application.name")).version("v1"));
    }

}
