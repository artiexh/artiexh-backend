package com.artiexh.authorization.client.config;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.auth.authentication.CookiesLogoutHandler;
import com.artiexh.auth.authentication.Http401UnauthorizedEntryPoint;
import com.artiexh.authorization.client.authentication.ArtiexhOAuth2UserService;
import com.artiexh.authorization.client.authentication.HttpCookieOAuth2AuthorizationRequestRepository;
import com.artiexh.authorization.client.authentication.OAuth2AuthenticationFailureHandler;
import com.artiexh.authorization.client.authentication.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity, HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository, ArtiexhOAuth2UserService artiexhOAuth2UserService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler, CookiesLogoutHandler cookiesLogoutHandler) throws Exception {
		httpSecurity
			.csrf(CsrfConfigurer::disable)
			.cors(CorsConfigurer::disable)
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
				.anyRequest().denyAll()
			)
			.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new Http401UnauthorizedEntryPoint())
			)
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
					.baseUri(Endpoint.OAuth2.ROOT + Endpoint.OAuth2.AUTHORIZATION)
					.authorizationRequestRepository(authorizationRequestRepository)
				)
				.redirectionEndpoint(redirectionEndpoint -> redirectionEndpoint
					.baseUri(Endpoint.OAuth2.ROOT + Endpoint.OAuth2.CALLBACK)
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
			);
		return httpSecurity.build();
	}

}
