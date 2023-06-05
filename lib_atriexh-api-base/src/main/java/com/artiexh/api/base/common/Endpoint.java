package com.artiexh.api.base.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Endpoint {

	public static final String PREFIX = "/api/v1";

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class OAuth2 {
		public static final String ROOT = PREFIX + "/oauth2";
		public static final String AUTHORIZATION = "/authorization";
		public static final String CALLBACK = "/callback/*";
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Auth {
		public static final String ROOT = PREFIX + "/auth";
		public static final String LOGIN = "/login";
		public static final String REFRESH = "/refresh";
		public static final String LOGOUT = "/logout";
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Registration {
		public static final String ROOT = PREFIX + "/registration";
		public static final String USER = "/user";
		public static final String PRINTER_PROVIDER = "/printer-provider";
		public static final String ARTIST = "/artist";
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Account {
		public static final String ROOT = PREFIX + "/account";
		public static final String ME = "/me";
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Product {
		public static final String ROOT = PREFIX + "/product";
		public static final String PRODUCT_DETAIL = "/{id}";
		public static final String PRODUCT_PAGE = "/page";
		public static final String PRODUCT_LIST = "/list";
	}
}
