package com.artiexh.auth;

import com.artiexh.auth.authentication.JwtAuthenticationProvider;
import com.artiexh.auth.jwt.JwtSecretKeyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableConfigurationProperties(JwtSecretKeyProperties.class)
public class AuthConfiguration {

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity, JwtAuthenticationProvider jwtAuthenticationProvider) throws Exception {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(jwtAuthenticationProvider);
        return builder.build();
    }

}
