package com.artiexh.api.service.impl;

import com.artiexh.api.service.AccountService;
import com.artiexh.data.jpa.entity.AccountEntity;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.UserEntity;
import com.artiexh.data.jpa.repository.AccountRepository;
import com.artiexh.data.jpa.repository.CartItemRepository;
import com.artiexh.data.jpa.repository.SubscriptionRepository;
import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.Role;
import com.artiexh.model.domain.User;
import com.artiexh.model.mapper.AccountMapper;
import com.artiexh.model.mapper.ArtistMapper;
import com.artiexh.model.mapper.UserMapper;
import com.artiexh.model.rest.account.AccountProfile;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final UserMapper userMapper;
	private final ArtistMapper artistMapper;
	private final AccountMapper accountMapper;
	private final SubscriptionRepository subscriptionRepository;
	private final CartItemRepository cartItemRepository;

	@Override
	@Transactional
	public Account getUserById(Long id) {
		return accountRepository.findById(id)
			.map(accountEntity -> switch (Role.fromValue(accountEntity.getRole())) {
				case ADMIN -> accountMapper.entityToDomain(accountEntity);
				case USER -> {
					User user = userMapper.entityToBasicUser((UserEntity) accountEntity);
					user.setCartItemCount(cartItemRepository.countAllByCartId(id));
					yield user;
				}
				case ARTIST -> {
					Artist artist = artistMapper.basicArtistInfo((ArtistEntity) accountEntity);
					artist.setCartItemCount(cartItemRepository.countAllByCartId(id));
					yield artist;
				}
				default -> null;
			})
			.orElse(null);
	}

	@Override
	public AccountProfile getUserProfile(Long id) {
		AccountEntity entity = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		return switch (Role.fromValue(entity.getRole())) {
			case USER -> {
				AccountProfile profile = userMapper.entityToAccountProfile((UserEntity) entity);
				profile.setNumOfSubscriptions(subscriptionRepository.countByUserId(entity.getId()));
				yield profile;
			}
			case ARTIST -> {
				AccountProfile profile = artistMapper.entityToAccountProfile((ArtistEntity) entity);
				profile.setNumOfSubscriptions(subscriptionRepository.countByUserId(entity.getId()));
				yield profile;
			}
			default -> null;
		};
	}

	@Override
	@Transactional
	public AccountProfile updateProfile(Long id, AccountProfile accountProfile) {
		AccountEntity entity = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		entity.setDisplayName(accountProfile.getDisplayName());
		entity.setEmail(accountProfile.getEmail());
		entity.setAvatarUrl(accountProfile.getAvatarUrl());

		accountRepository.save(entity);
		AccountProfile profile = userMapper.entityToAccountProfile((UserEntity) entity);
		profile.setNumOfSubscriptions(subscriptionRepository.countByUserId(entity.getId()));
		return profile;
	}
}
