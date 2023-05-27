package com.artiexh.authorization.client.service.impl;

import com.artiexh.authorization.client.service.RegistrationService;
import com.artiexh.data.jpa.repository.UserRepository;
import com.artiexh.model.domain.User;
import com.artiexh.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	private final UserMapper userMapper;
	private final UserRepository userRepository;

	@Override
	public User createUser(User user) {
		var userEntity = userRepository.save(userMapper.domainToEntity(user));
		return userMapper.entityToDomain(userEntity);
	}

}
