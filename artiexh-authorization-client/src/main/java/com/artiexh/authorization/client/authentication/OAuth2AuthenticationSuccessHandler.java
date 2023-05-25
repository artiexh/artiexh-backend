package com.artiexh.authorization.client.authentication;

import com.artiexh.auth.common.CookieUtil;
import com.artiexh.auth.jwt.JwtConfiguration;
import com.artiexh.auth.jwt.JwtProcessor;
import com.artiexh.auth.service.ActiveTokenService;
import com.artiexh.model.domain.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static com.artiexh.auth.common.AuthConstant.*;

@Component
@Log4j2
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
	private final JwtProcessor jwtProcessor;
	private final JwtConfiguration jwtConfiguration;
	private final ActiveTokenService activeTokenService;
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public OAuth2AuthenticationSuccessHandler(HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository, JwtProcessor jwtProcessor, JwtConfiguration jwtConfiguration, ActiveTokenService activeTokenService) {
		this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
		this.jwtProcessor = jwtProcessor;
		this.jwtConfiguration = jwtConfiguration;
		this.activeTokenService = activeTokenService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		String targetUrl = CookieUtil.getCookies(request, REDIRECT_URI_PARAM_COOKIE_NAME)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found or invalid redirect uri"));

		if (response.isCommitted()) {
			log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		Role role = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.findFirst()
			.map(Role::valueOf)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no role"));

		String accessToken = jwtProcessor.encode(authentication.getName(), role, JwtProcessor.TokenType.ACCESS_TOKEN);
		CookieUtil.addCookies(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) jwtConfiguration.getAccessTokenExpiration().getSeconds());

		String refreshToken = jwtProcessor.encode(authentication.getName(), role, JwtProcessor.TokenType.REFRESH_TOKEN);
		CookieUtil.addCookies(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) jwtConfiguration.getRefreshTokenExpiration().getSeconds());

		activeTokenService.put(authentication.getName(), accessToken, refreshToken);

		clearAuthenticationAttributes(request, response);
		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequest(request, response);
	}
}
