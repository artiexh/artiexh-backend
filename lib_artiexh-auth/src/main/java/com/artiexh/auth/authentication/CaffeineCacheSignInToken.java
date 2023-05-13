package com.artiexh.auth.authentication;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CaffeineCacheSignInToken implements SignInToken {

    private static final String SIGN_IN_TOKEN = "signInToken";
    private final Cache cache;

    public CaffeineCacheSignInToken(CaffeineCacheManager cacheManager) {
        cacheManager.registerCustomCache(
                SIGN_IN_TOKEN,
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .build()
        );
        this.cache = cacheManager.getCache(SIGN_IN_TOKEN);
    }

    @Override
    public void add(String accessToken) {
        cache.put(accessToken, accessToken);
    }

    @Override
    public boolean isExist(String accessToken) {
        return cache.get(accessToken) != null;
    }

    @Override
    public void remove(String accessToken) {
        cache.evict(accessToken);
    }
}
