package com.artiexh.model.mapper;

import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class PasswordMapper {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Named("encodedPassword")
	protected String encodedPassword(String password) {
		if (password == null) {
			return null;
		}
		return passwordEncoder.encode(password);
	}

}
