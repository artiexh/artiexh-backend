package com.artiexh.auth.authentication;

public interface SignInToken {
    void add(String accessToken);

    boolean isExist(String accessToken);

    void remove(String accessToken);
}
