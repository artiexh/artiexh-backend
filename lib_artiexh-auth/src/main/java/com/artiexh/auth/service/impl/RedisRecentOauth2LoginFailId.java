package com.artiexh.auth.service.impl;

import com.artiexh.auth.service.RecentOauth2LoginFailId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class RedisRecentOauth2LoginFailId implements RecentOauth2LoginFailId {
	private static final String PREFIX = "recent_oauth2_";

	private final RedisTemplate<String, Long> redisTemplate;

	@Value("${artiexh.security.recent_oauth2_fail_id.expiration}")
	@DurationUnit(ChronoUnit.MINUTES)
	private Duration expiration;

	public RedisRecentOauth2LoginFailId(@Qualifier("authRedis") RedisConnectionFactory redisConnectionFactory) {
		this.redisTemplate = new RedisTemplate<>();
		this.redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.afterPropertiesSet();
	}

	@Override
	public void put(String providerId, String sub) {
		redisTemplate.boundValueOps(PREFIX + providerId + '_' + sub).set(Instant.now().plus(expiration).toEpochMilli(), expiration);
	}

	@Override
	public Boolean remove(String providerId, String sub) {
		return redisTemplate.delete(PREFIX + providerId + '_' + sub);
	}

	@Override
	public boolean contain(String providerId, String sub) {
		Long expiredAt = redisTemplate.boundValueOps(PREFIX + providerId + '_' + sub).get();
		return expiredAt != null && expiredAt > Instant.now().toEpochMilli();
	}

}
