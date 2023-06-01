package com.artiexh.authorization.client.service.impl;

import com.artiexh.auth.service.RecentOauth2LoginFailId;
import com.artiexh.authorization.client.service.RegistrationService;
import com.artiexh.data.jpa.entity.PrinterProviderEntity;
import com.artiexh.data.jpa.entity.UserEntity;
import com.artiexh.data.jpa.repository.PrinterProviderRepository;
import com.artiexh.data.jpa.repository.UserRepository;
import com.artiexh.model.domain.PrinterProvider;
import com.artiexh.model.domain.User;
import com.artiexh.model.mapper.PrinterProviderMapper;
import com.artiexh.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	private final UserMapper userMapper;
	private final PrinterProviderMapper printerProviderMapper;
	private final UserRepository userRepository;
	private final PrinterProviderRepository printerProviderRepository;
	private final RecentOauth2LoginFailId recentOauth2LoginFailId;

	@Override
	public User createUser(User user) {
		UserEntity userEntity = userMapper.domainToEntity(user);

		if (!StringUtils.hasText(userEntity.getTwitterId()) || !recentOauth2LoginFailId.contain("twitter", userEntity.getTwitterId())) {
			userEntity.setTwitterId(null);
		} else {
			recentOauth2LoginFailId.remove("twitter", userEntity.getTwitterId());
		}
		if (!StringUtils.hasText(userEntity.getGoogleId()) || !recentOauth2LoginFailId.contain("google", userEntity.getGoogleId())) {
			userEntity.setGoogleId(null);
		} else {
			recentOauth2LoginFailId.remove("google", userEntity.getGoogleId());
		}
		if (!StringUtils.hasText(userEntity.getFacebookId()) || !recentOauth2LoginFailId.contain("facebook", userEntity.getFacebookId())) {
			userEntity.setFacebookId(null);
		} else {
			recentOauth2LoginFailId.remove("facebook", userEntity.getFacebookId());
		}

		if (!StringUtils.hasText(userEntity.getPassword()) &&
			!StringUtils.hasText(userEntity.getTwitterId()) &&
			!StringUtils.hasText(userEntity.getGoogleId()) &&
			!StringUtils.hasText(userEntity.getFacebookId())) {
			throw new IllegalArgumentException("Request has no password or invalid provider sub");
		}

		return userMapper.entityToDomain(userRepository.save(userEntity));
	}

	@Override
	public PrinterProvider createPrinterProvider(PrinterProvider printerProvider) {
		PrinterProviderEntity entity = printerProviderRepository.save(printerProviderMapper.domainToEntity(printerProvider));
		return printerProviderMapper.entityToDomain(entity);
	}

}
