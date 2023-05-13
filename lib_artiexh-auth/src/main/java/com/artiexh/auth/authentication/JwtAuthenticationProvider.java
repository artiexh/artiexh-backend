package com.artiexh.auth.authentication;

import com.artiexh.auth.jwt.JweDecrypter;
import com.artiexh.model.domain.Role;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
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
import org.springframework.util.StringUtils;

import java.text.ParseException;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final SignInToken signInToken;
    private final JweDecrypter jweDecrypter;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        String token = authenticationToken.getCredentials().toString();

        if (isSignedOut(token)) {
            log.trace("Token is signed out");
            throw new DisabledException("Token is signed out");
        }

        EncryptedJWT decryptedToken;
        try {
            decryptedToken = jweDecrypter.decrypt(token);
        } catch (JOSEException | ParseException ex) {
            log.trace("Failed to parse or decrypt token", ex);
            throw new BadCredentialsException("Failed to parse token", ex);
        }

        JWTClaimsSet claims;
        try {
            claims = decryptedToken.getJWTClaimsSet();
        } catch (ParseException ex) {
            log.trace("JWE payload is not json format", ex);
            throw new BadCredentialsException("JWE payload is not json format", ex);
        }

        String sub = claims.getSubject();
        if (!StringUtils.hasText(sub)) {
            log.trace("Token does not contain subject claim");
            throw new BadCredentialsException("Token does not contain subject claim");
        }

        String role;
        try {
            role = claims.getStringClaim("role");
            Role.valueOf(role);
        } catch (ParseException | IllegalArgumentException e) {
            log.trace("Token does not contain valid role claim");
            throw new BadCredentialsException("Token does not contain valid role claim");
        }

        GrantedAuthority authorities = new SimpleGrantedAuthority(role);
        return new JwtAuthenticationToken(Long.valueOf(sub), token, claims, authorities);
    }

    private boolean isSignedOut(String token) {
        return !signInToken.isExist(token);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
