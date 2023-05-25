package com.artiexh.model.domain;

public enum OAuth2Provider {
	GOOGLE("google"),
	FACEBOOK("facebook"),
	TWITTER("twitter");

	private final String value;

	OAuth2Provider(String value) {
		this.value = value;
	}

	public static OAuth2Provider fromValue(String value) {
		for (OAuth2Provider provider : OAuth2Provider.values()) {
			if (provider.getValue().equals(value)) {
				return provider;
			}
		}
		throw new IllegalArgumentException("No OAuth2Provider with value " + value + " found");
	}

	public String getValue() {
		return value;
	}
}
