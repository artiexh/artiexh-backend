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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
										   HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository,
										   ArtiexhOAuth2UserService artiexhOAuth2UserService,
										   OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
										   OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
										   CookiesLogoutHandler cookiesLogoutHandler,
										   Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint) throws Exception {
		httpSecurity
			.csrf().disable()
			.cors().and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.formLogin().disable()
			.httpBasic().disable()
			.authorizeHttpRequests(authz -> authz
				.requestMatchers("/actuator/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/api/v1/oauth2/**").permitAll()
				.requestMatchers("/api/v1/registration/**").permitAll()
				.requestMatchers("/api/v1/auth/login").permitAll()
				.anyRequest().denyAll()
			)
			.exceptionHandling(exceptionHandling -> exceptionHandling
				.authenticationEntryPoint(http401UnauthorizedEntryPoint)
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
