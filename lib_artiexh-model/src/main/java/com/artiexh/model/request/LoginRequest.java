package com.artiexh.model.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String accessToken) {
}
