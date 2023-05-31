package com.artiexh.api.config;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.auth.authentication.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
										   AuthenticationManager authenticationManager,
										   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
		httpSecurity
			.authenticationManager(authenticationManager)
			.csrf(CsrfConfigurer::disable)
			.cors(CorsConfigurer::disable)
			.addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(FormLoginConfigurer::disable)
			.httpBasic(HttpBasicConfigurer::disable)
			.authorizeHttpRequests(authz -> authz
				.requestMatchers("/test/**").permitAll()
				.requestMatchers("/actuator/**").permitAll()
				.requestMatchers("/swagger*/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers(Endpoint.Registration.ROOT + "/**").permitAll()
				.requestMatchers(Endpoint.Auth.ROOT + "/**").permitAll()
				.anyRequest().authenticated()
			);
		return httpSecurity.build();
	}

}
