package com.artiexh.auth.service;

import com.artiexh.auth.jwt.JwtConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisActiveTokenService implements ActiveTokenService {
	private final StringRedisTemplate redisTemplate;
	private final JwtConfiguration jwtConfiguration;

	public RedisActiveTokenService(RedisProperties redisProperties, JwtConfiguration jwtConfiguration) {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
		configuration.setPassword(redisProperties.getPassword());
		configuration.setDatabase(1);
		LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration);
		connectionFactory.afterPropertiesSet();
		redisTemplate = new StringRedisTemplate(connectionFactory);
		this.jwtConfiguration = jwtConfiguration;
	}

	@Override
	public void put(String userId, String accessToken, String refreshToken) {
		redisTemplate.boundValueOps(userId + "_access").set(accessToken, jwtConfiguration.getAccessTokenExpiration());
		redisTemplate.boundValueOps(userId + "_refresh").set(refreshToken, jwtConfiguration.getRefreshTokenExpiration());
	}

	@Override
	public void remove(String userId) {
		redisTemplate.delete(userId + "_*");
	}

	@Override
	public boolean containAccessToken(String userId, String accessToken) {
		String existedUserAccessToken = redisTemplate.boundValueOps(userId + "_access").get();
		return accessToken.equals(existedUserAccessToken);
	}

	@Override
	public boolean containRefreshToken(String userId, String refreshToken) {
		String existedUserRefreshToken = redisTemplate.boundValueOps(userId + "_refresh").get();
		return refreshToken.equals(existedUserRefreshToken);
	}
}
