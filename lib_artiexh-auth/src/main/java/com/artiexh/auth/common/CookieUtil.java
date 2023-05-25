package com.artiexh.auth.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieUtil {

	public static final String DEFAULT_PATH = "/";

	public static final int DEFAULT_EXPIRE = 180;

	public static Optional<String> getCookies(HttpServletRequest request, String name) {
		return Arrays.stream(request.getCookies())
			.filter(cookie -> name.equals(cookie.getName()))
			.map(Cookie::getValue)
			.findAny();
	}

	public static void addCookies(HttpServletResponse response, String name, String value) {
		addCookies(response, name, value, DEFAULT_PATH, DEFAULT_EXPIRE);
	}

	public static void addCookies(HttpServletResponse response, String name, String value, String path) {
		addCookies(response, name, value, path, DEFAULT_EXPIRE);
	}

	public static void addCookies(HttpServletResponse response, String name, String value, int expire) {
		addCookies(response, name, value, DEFAULT_PATH, expire);
	}

	public static void addCookies(HttpServletResponse response, String name, String value, String path, int expire) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(path);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(expire);
		response.addCookie(cookie);
	}

	public static void deleteCookies(HttpServletRequest request, HttpServletResponse response, String... names) {
		List<String> nameList = Arrays.asList(names);
		Arrays.stream(request.getCookies())
			.filter(cookie -> nameList.contains(cookie.getName()))
			.forEach(cookie -> {
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			});
	}

}
