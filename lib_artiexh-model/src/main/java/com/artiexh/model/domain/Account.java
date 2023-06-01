package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Account {
	private Long id;
	private String username;
	@JsonIgnore
	private String password;
	private Role role;
	private UserStatus status;
	private String avatarUrl;
	private String email;
}
