package com.artiexh.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
	@NotBlank String username,
	@NotBlank String password,
	String avatarUrl,
	String email,
	@JsonIgnore String googleId,
	@JsonIgnore String twitterId,
	@JsonIgnore String facebookId
) {
}
