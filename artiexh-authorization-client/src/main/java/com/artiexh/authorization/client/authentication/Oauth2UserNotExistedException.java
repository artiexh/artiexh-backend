package com.artiexh.authorization.client.authentication;

import org.springframework.security.core.AuthenticationException;

import java.util.Map;

public class Oauth2UserNotExistedException extends AuthenticationException {

	private final String providerId;
	private final Map<String, Object> attributes;

	public Oauth2UserNotExistedException(String msg, String providerId, Map<String, Object> attributes) {
		super(msg);
		this.attributes = attributes;
		this.providerId = providerId;
	}

	public String getProviderId() {
		return providerId;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
