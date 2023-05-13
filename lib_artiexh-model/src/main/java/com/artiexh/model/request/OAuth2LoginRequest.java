package com.artiexh.model.request;

import com.artiexh.model.domain.OAuth2Provider;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record OAuth2LoginRequest(
        String name,
        String email,
        String picture,
        @NotBlank String sub,
        Instant iat,
        Instant exp,
        UUID jit,
        OAuth2Provider provider
) {
}
