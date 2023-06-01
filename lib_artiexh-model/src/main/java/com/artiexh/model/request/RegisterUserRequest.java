package com.artiexh.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

	@NotBlank(message = "Username is required")
	private String username;

	private String password;

	private String avatarUrl;

	private String email;

	private String googleId;

	private String twitterId;

	private String facebookId;

}
