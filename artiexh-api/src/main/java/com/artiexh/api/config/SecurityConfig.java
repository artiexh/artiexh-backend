package com.artiexh.api.config;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.auth.authentication.JwtAuthenticationFilter;
import com.artiexh.auth.authentication.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
										   JwtAuthenticationFilter jwtAuthenticationFilter,
										   JwtAuthenticationProvider jwtAuthenticationProvider,
										   AuthenticationEntryPoint http401UnauthorizedEntryPoint) throws Exception {
		httpSecurity.authenticationProvider(jwtAuthenticationProvider);
		httpSecurity
			.addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.csrf(CsrfConfigurer::disable)
			.cors(Customizer.withDefaults())
			.sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(FormLoginConfigurer::disable)
			.httpBasic(HttpBasicConfigurer::disable)
			.authorizeHttpRequests(authz -> authz
				.requestMatchers("/actuator/**").permitAll()
				.requestMatchers("/swagger*/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers(HttpMethod.GET, Endpoint.Product.ROOT, Endpoint.Product.ROOT + Endpoint.Product.PRODUCT_DETAIL).permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling(exceptionHandling ->
				exceptionHandling.authenticationEntryPoint(http401UnauthorizedEntryPoint)
			);
		return httpSecurity.build();
	}

}
