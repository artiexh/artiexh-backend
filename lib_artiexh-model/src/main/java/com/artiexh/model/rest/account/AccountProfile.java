package com.artiexh.model.rest.account;

import com.artiexh.model.domain.Role;
import com.artiexh.model.domain.UserStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class AccountProfile {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private String username;
	private String displayName;
	private Role role;
	private UserStatus status;
	private String avatarUrl;
	private String email;
	private Long numOfSubscriptions;
}
