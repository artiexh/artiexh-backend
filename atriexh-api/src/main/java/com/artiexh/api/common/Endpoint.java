package com.artiexh.api.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Endpoint {

    public static final String PREFIX = "/api/v1";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Registration {
        public static final String ROOT = PREFIX + "/registration";
        public static final String USER = "/user";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Auth {
        public static final String ROOT = PREFIX + "/auth";
        public static final String VERIFY = "/verify";
        public static final String LOGIN = "/login";
    }
}
