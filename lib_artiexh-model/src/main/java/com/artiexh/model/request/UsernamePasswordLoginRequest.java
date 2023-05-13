package com.artiexh.model.request;

public record UsernamePasswordLoginRequest(
        String username,
        String password
) {
}
