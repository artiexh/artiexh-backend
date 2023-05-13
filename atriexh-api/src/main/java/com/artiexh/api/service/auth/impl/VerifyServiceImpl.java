package com.artiexh.api.service.auth.impl;

import com.artiexh.api.service.auth.VerifyUserService;
import com.artiexh.data.jpa.repository.UserRepository;
import com.artiexh.model.domain.User;
import com.artiexh.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class VerifyServiceImpl implements VerifyUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> verifyFromUsernamePassword(String username, String password) {
        return userRepository.findByUsername(username)
                .map(userEntity -> {
                    //var encodedPassword = passwordEncoder.encode(userEntity.getPassword());
                    if (passwordEncoder.matches(password, userEntity.getPassword())) {
                        return userMapper.entityToDomain(userEntity);
                    }
                    return null;
                });
    }

    @Override
    public Optional<User> verifyFromGoogleProvider(String sub) {
        return userRepository.findByGoogleId(Long.valueOf(sub))
                .map(userMapper::entityToDomain);
    }

    @Override
    public Optional<User> verifyFromFacebookProvider(String sub) {
        return userRepository.findByFacebookId(Long.valueOf(sub))
                .map(userMapper::entityToDomain);
    }

    @Override
    public Optional<User> verifyFromTwitterProvider(String sub) {
        return userRepository.findByTwitterId(Long.valueOf(sub))
                .map(userMapper::entityToDomain);
    }

}
