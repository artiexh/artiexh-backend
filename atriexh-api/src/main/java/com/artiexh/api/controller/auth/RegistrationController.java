package com.artiexh.api.controller.auth;

import com.artiexh.api.common.Endpoint;
import com.artiexh.api.service.auth.UserService;
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

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping(Endpoint.Registration.USER)
    public User register(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        return userService.createUser(userMapper.registerUserRequestToDomain(registerUserRequest));
    }

}
