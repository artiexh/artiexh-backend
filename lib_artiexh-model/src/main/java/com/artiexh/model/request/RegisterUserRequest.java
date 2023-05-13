package com.artiexh.model.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        String avatarUrl,
        String email,
        String googleId,
        String twitterId,
        String facebookId
) {
}
