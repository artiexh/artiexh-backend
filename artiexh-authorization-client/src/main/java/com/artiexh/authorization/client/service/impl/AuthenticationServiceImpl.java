package com.artiexh.authorization.client.service.impl;

import com.artiexh.authorization.client.service.AuthenticationService;
import com.artiexh.data.jpa.repository.UserRepository;
import com.artiexh.model.domain.User;
import com.artiexh.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(userMapper::entityToDomain);
    }

}
