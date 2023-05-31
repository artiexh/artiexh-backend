package com.artiexh.authorization.client.config;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.auth.authentication.CookiesLogoutHandler;
import com.artiexh.auth.authentication.JwtAuthenticationFilter;
import com.artiexh.auth.authentication.JwtAuthenticationProvider;
import com.artiexh.authorization.client.authentication.ArtiexhOAuth2UserService;
import com.artiexh.authorization.client.authentication.HttpCookieOAuth2AuthorizationRequestRepository;
import com.artiexh.authorization.client.authentication.OAuth2AuthenticationFailureHandler;
import com.artiexh.authorization.client.authentication.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
										   JwtAuthenticationFilter jwtAuthenticationFilter,
										   HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository,
										   ArtiexhOAuth2UserService artiexhOAuth2UserService,
										   OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
										   OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
										   CookiesLogoutHandler cookiesLogoutHandler,
										   AuthenticationEntryPoint http401UnauthorizedEntryPoint,
										   LogoutSuccessHandler logoutSuccessHandler,
										   JwtAuthenticationProvider jwtAuthenticationProvider) throws Exception {
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
				.requestMatchers(Endpoint.OAuth2.ROOT + "/**").permitAll()
				.requestMatchers(Endpoint.Registration.ROOT + "/**").permitAll()
				.requestMatchers(Endpoint.Auth.ROOT + Endpoint.Auth.LOGIN).permitAll()
				.requestMatchers(Endpoint.Auth.ROOT + Endpoint.Auth.REFRESH).permitAll()
				.anyRequest().authenticated()
			)
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
					.baseUri(Endpoint.OAuth2.ROOT + Endpoint.OAuth2.AUTHORIZATION)
					.authorizationRequestRepository(authorizationRequestRepository)
				)
				.redirectionEndpoint(redirectionEndpoint -> redirectionEndpoint
					.baseUri("/api/v1/oauth2/callback/*")
				)
				.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
					.userService(artiexhOAuth2UserService)
				)
				.successHandler(oAuth2AuthenticationSuccessHandler)
				.failureHandler(oAuth2AuthenticationFailureHandler)
			)
			.logout(logout -> logout
				.logoutUrl(Endpoint.Auth.ROOT + Endpoint.Auth.LOGOUT)
				.addLogoutHandler(cookiesLogoutHandler)
				.logoutSuccessHandler(logoutSuccessHandler)
			)
			.exceptionHandling(exceptionHandling ->
				exceptionHandling.authenticationEntryPoint(http401UnauthorizedEntryPoint)
			);
		return httpSecurity.build();
	}

}
