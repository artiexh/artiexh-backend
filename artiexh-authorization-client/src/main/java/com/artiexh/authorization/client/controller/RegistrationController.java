package com.artiexh.authorization.client.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.authorization.client.service.RegistrationService;
import com.artiexh.model.domain.User;
import com.artiexh.model.mapper.UserMapper;
import com.artiexh.model.request.RegisterUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoint.Registration.ROOT)
@RequiredArgsConstructor
public class RegistrationController {

	private final UserMapper userMapper;
	private final RegistrationService registrationService;

	@PostMapping(Endpoint.Registration.USER)
	public User register(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
		return registrationService.createUser(userMapper.registerUserRequestToDomain(registerUserRequest));
	}

}
