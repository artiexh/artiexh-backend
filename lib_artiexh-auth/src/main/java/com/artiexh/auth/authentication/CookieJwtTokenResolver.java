package com.artiexh.auth.authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Arrays;

public class CookieJwtTokenResolver implements JwtTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("next-auth.session-token"))
                .findAny()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Token not found"));
    }

}
