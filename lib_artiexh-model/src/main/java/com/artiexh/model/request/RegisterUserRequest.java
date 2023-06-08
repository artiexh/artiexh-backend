package com.artiexh.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

	@NotBlank(message = "Username is required")
	@Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
	@Pattern(regexp = "^[^\\s+]*$", message = "Username must be exclude whitespaces")
	private String username;

	@Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
	@Pattern(regexp = "^[^\\s+]*$", message = "Password must be exclude whitespaces")
	private String password;

	@NotBlank(message = "Display name is required")
	@Size(min = 4, max = 50, message = "Display name must be between 4 and 50 characters")
	private String displayName;

	@Size(max = 2048, message = "Avatar URL must be less than 2048 characters")
	private String avatarUrl;

	@Email
	private String email;

	@Pattern(regexp = "^[0-9]{21}$", message = "Invalid google id")
	private String googleId;

	@Pattern(regexp = "^[0-9]{20}$", message = "Invalid twitter id")
	private String twitterId;

	@Pattern(regexp = "^[0-9]{16}$", message = "Invalid facebook id")
	private String facebookId;

}
