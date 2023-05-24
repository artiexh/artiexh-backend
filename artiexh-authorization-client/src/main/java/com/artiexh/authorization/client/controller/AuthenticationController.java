package com.artiexh.authorization.client.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.auth.common.CookieUtil;
import com.artiexh.auth.jwt.JwtConfiguration;
import com.artiexh.auth.jwt.JwtProcessor;
import com.artiexh.authorization.client.service.AuthenticationService;
import com.artiexh.model.domain.User;
import com.artiexh.model.request.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static com.artiexh.auth.common.AuthConstant.ACCESS_TOKEN_COOKIE_NAME;
import static com.artiexh.auth.common.AuthConstant.REFRESH_TOKEN_COOKIE_NAME;

@RestController
@RequestMapping(Endpoint.Auth.ROOT)
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtConfiguration jwtConfiguration;
    private final JwtProcessor jwtProcessor;

    @PostMapping(Endpoint.Auth.LOGIN)
    public User login(HttpServletResponse response, @RequestBody @Valid LoginRequest loginRequest) {
        User user = authenticationService.login(loginRequest.username(), loginRequest.password())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        String accessToken = jwtProcessor.encode(user.getId().toString(), user.getRole(), JwtProcessor.TokenType.ACCESS_TOKEN);
        CookieUtil.addCookies(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) jwtConfiguration.getAccessTokenExpiration().getSeconds());

        String refreshToken = jwtProcessor.encode(user.getId().toString(), user.getRole(), JwtProcessor.TokenType.REFRESH_TOKEN);
        CookieUtil.addCookies(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) jwtConfiguration.getRefreshTokenExpiration().getSeconds());

        return user;
    }

}
