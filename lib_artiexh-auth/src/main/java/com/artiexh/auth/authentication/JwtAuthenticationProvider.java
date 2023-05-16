package com.artiexh.auth.authentication;

import com.artiexh.auth.jwt.JwtProcessor;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProcessor jwtProcessor;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        String token = authenticationToken.getCredentials().toString();

        if (isSignedOut(token)) {
            log.trace("Token is signed out");
            throw new DisabledException("Token is signed out");
        }

        DecodedJWT decodedJwt;
        try {
            decodedJwt = jwtProcessor.decode(token);
        } catch (JWTVerificationException ex) {
            log.trace("Failed to parse or decode token", ex);
            throw new BadCredentialsException("Failed to parse token", ex);
        }

        String sub = decodedJwt.getSubject();
        String authority = decodedJwt.getClaim("authority").asString();

        GrantedAuthority authorities = new SimpleGrantedAuthority(authority);
        return new JwtAuthenticationToken(Long.valueOf(sub), token, decodedJwt, authorities);
    }

    private boolean isSignedOut(String token) {
        return false;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
