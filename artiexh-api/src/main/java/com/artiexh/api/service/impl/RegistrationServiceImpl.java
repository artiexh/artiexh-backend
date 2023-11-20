package com.artiexh.api.service.impl;

import com.artiexh.api.service.RegistrationService;
import com.artiexh.auth.service.RecentOauth2LoginFailId;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.Role;
import com.artiexh.model.domain.User;
import com.artiexh.model.mapper.AccountMapper;
import com.artiexh.model.mapper.ArtistMapper;
import com.artiexh.model.mapper.StaffMapper;
import com.artiexh.model.mapper.UserMapper;
import com.artiexh.model.rest.artist.request.RegistrationArtistRequest;
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
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final ArtistRepository artistRepository;
	private final CartRepository cartRepository;
	private final WardRepository wardRepository;
	private final RecentOauth2LoginFailId recentOauth2LoginFailId;
	private final StaffRepository staffRepository;
	private final StaffMapper staffMapper;

	@Override
	public User createUser(User user) {
		UserEntity userEntity = userMapper.domainToEntity(user);

		List<Pair<String, String>> cacheProviderSubKeys = new ArrayList<>();
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

		return userMapper.entityToBasicUser(savedUserEntity);
	}

	@Override
	public Artist registerArtist(Long id, RegistrationArtistRequest request) {
		UserEntity userEntity = userRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);

		if (userEntity instanceof ArtistEntity existedArtistEntity) {
			return artistMapper.basicArtistInfo(existedArtistEntity);
		}

		if ((int) userEntity.getRole() != Role.USER.getValue()) {
			throw new IllegalArgumentException("User role is not USER, cannot register as ARTIST");
		}

		artistRepository.createArtistByExistedUserId(id);
		ArtistEntity artistEntity = artistRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);

		artistEntity.setBankAccount(request.getBankAccount());
		artistEntity.setBankName(request.getBankName());
		artistEntity.setPhone(request.getPhone());
		artistEntity.setShopThumbnailUrl(request.getShopThumbnailUrl());
		artistEntity.setDescription(request.getDescription());

		artistEntity.setRole((byte) Role.ARTIST.getValue());
		return artistMapper.basicArtistInfo(artistRepository.save(artistEntity));
	}

	@Override
	@Transactional
	public Account createAdmin(Account account) {
		StaffEntity savedAccountEntity = staffRepository.save(staffMapper.domainToEntity(account));
		return accountMapper.entityToDomain(savedAccountEntity);
	}

}
