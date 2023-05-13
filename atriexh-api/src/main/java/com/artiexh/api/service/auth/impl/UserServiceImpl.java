package com.artiexh.api.service.auth.impl;

import com.artiexh.api.service.auth.UserService;
import com.artiexh.data.jpa.repository.UserRepository;
import com.artiexh.model.domain.User;
import com.artiexh.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User createUser(User user) {
        var userEntity = userRepository.save(userMapper.domainToEntity(user));
        return userMapper.entityToDomain(userEntity);
    }

}
