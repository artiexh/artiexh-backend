package com.artiexh.authorization.client.service.impl;

import com.artiexh.auth.service.RecentOauth2LoginFailId;
import com.artiexh.authorization.client.service.RegistrationService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.*;
import com.artiexh.model.mapper.AccountMapper;
import com.artiexh.model.mapper.ArtistMapper;
import com.artiexh.model.mapper.PrinterProviderMapper;
import com.artiexh.model.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

	private final AccountMapper accountMapper;
	private final UserMapper userMapper;
	private final ArtistMapper artistMapper;
	private final PrinterProviderMapper printerProviderMapper;
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final ArtistRepository artistRepository;
	private final PrinterProviderRepository printerProviderRepository;
	private final CartRepository cartRepository;
	private final RecentOauth2LoginFailId recentOauth2LoginFailId;

	@Override
	public User createUser(User user) {
		UserEntity userEntity = userMapper.domainToEntity(user);

		List<Pair<String, String>> cacheProviderSubKeys = new ArrayList<>();
		if (!StringUtils.hasText(userEntity.getTwitterId()) || !recentOauth2LoginFailId.contain("twitter", userEntity.getTwitterId())) {
			userEntity.setTwitterId(null);
		} else {
			cacheProviderSubKeys.add(Pair.of("twitter", userEntity.getTwitterId()));
		}
		if (!StringUtils.hasText(userEntity.getGoogleId()) || !recentOauth2LoginFailId.contain("google", userEntity.getGoogleId())) {
			userEntity.setGoogleId(null);
		} else {
			cacheProviderSubKeys.add(Pair.of("google", userEntity.getGoogleId()));
		}
		if (!StringUtils.hasText(userEntity.getFacebookId()) || !recentOauth2LoginFailId.contain("facebook", userEntity.getFacebookId())) {
			userEntity.setFacebookId(null);
		} else {
			cacheProviderSubKeys.add(Pair.of("facebook", userEntity.getFacebookId()));
		}

		if (!StringUtils.hasText(userEntity.getPassword()) &&
			!StringUtils.hasText(userEntity.getTwitterId()) &&
			!StringUtils.hasText(userEntity.getGoogleId()) &&
			!StringUtils.hasText(userEntity.getFacebookId())) {
			throw new IllegalArgumentException("Request has no password or invalid provider sub");
		}

		accountRepository.findByUsername(userEntity.getUsername())
			.ifPresent(existedUserEntity -> {
				throw new IllegalArgumentException("Existed username");
			});
		UserEntity savedUserEntity = userRepository.save(userEntity);
		CartEntity cartEntity = CartEntity.builder().id(savedUserEntity.getId()).build();
		cartRepository.save(cartEntity);

		for (Pair<String, String> cacheProviderSubKey : cacheProviderSubKeys) {
			recentOauth2LoginFailId.remove(cacheProviderSubKey.getFirst(), cacheProviderSubKey.getSecond());
		}

		return userMapper.entityToDomain(savedUserEntity);
	}

	@Override
	public PrinterProvider createPrinterProvider(PrinterProvider printerProvider) {
		PrinterProviderEntity entity = printerProviderRepository.save(printerProviderMapper.domainToEntity(printerProvider));
		return printerProviderMapper.entityToDomain(entity);
	}

	@Override
	public Artist registerArtist(Long id) {
		UserEntity userEntity = userRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);

		if (userEntity instanceof ArtistEntity existedArtistEntity) {
			return artistMapper.entityToDomain(existedArtistEntity);
		}

		if ((int) userEntity.getRole() != Role.USER.getValue()) {
			throw new IllegalArgumentException("User role is not USER, cannot register as ARTIST");
		}

		artistRepository.createArtistByExistedUserId(id);
		ArtistEntity artistEntity = artistRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);

		artistEntity.setRole((byte) Role.ARTIST.getValue());
		return artistMapper.entityToDomain(artistRepository.save(artistEntity));
	}

	@Override
	public Account createAdmin(Account account) {
		AccountEntity savedAccountEntity = accountRepository.save(accountMapper.domainToEntity(account));
		return accountMapper.entityToDomain(savedAccountEntity);
	}

}
