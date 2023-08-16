package com.artiexh.api.service.impl;

import com.artiexh.api.service.AccountService;
import com.artiexh.data.jpa.entity.AccountEntity;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.UserEntity;
import com.artiexh.data.jpa.repository.AccountRepository;
import com.artiexh.data.jpa.repository.SubscriptionRepository;
import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.Role;
import com.artiexh.model.mapper.AccountMapper;
import com.artiexh.model.mapper.ArtistMapper;
import com.artiexh.model.mapper.UserMapper;
import com.artiexh.model.rest.account.AccountProfile;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.artiexh.model.domain.Role.ADMIN;
import static com.artiexh.model.domain.Role.USER;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final UserMapper userMapper;
	private final ArtistMapper artistMapper;
	private final AccountMapper accountMapper;
	private final SubscriptionRepository subscriptionRepository;

	@Override
	@Transactional
	public Account getUserById(Long id) {
		return accountRepository.findById(id)
			.map(accountEntity -> switch (Role.fromValue(accountEntity.getRole())) {
				case ADMIN -> accountMapper.entityToDomain(accountEntity);
				case USER -> userMapper.entityToDomain((UserEntity) accountEntity);
				case ARTIST -> artistMapper.entityToDomain((ArtistEntity) accountEntity);
			})
			.orElse(null);
	}

	@Override
	public AccountProfile getUserProfile(Long id) {
		AccountEntity entity = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		switch (Role.fromValue(entity.getRole())) {
			case USER -> {
				return accountMapper.entityToResponse(entity);
			}
			case ARTIST -> {
				AccountProfile profile = accountMapper.entityToResponse(entity);
				profile.setNumOfSubscriptions(subscriptionRepository.countByArtistId(entity.getId()));
				return profile;
			}
			default -> {
				return null;
			}
		}
	}
}
