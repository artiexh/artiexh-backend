package com.artiexh.api.service.impl;

import com.artiexh.api.service.AccountService;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.PrinterProviderEntity;
import com.artiexh.data.jpa.entity.UserEntity;
import com.artiexh.data.jpa.repository.AccountRepository;
import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.Role;
import com.artiexh.model.mapper.AccountMapper;
import com.artiexh.model.mapper.ArtistMapper;
import com.artiexh.model.mapper.PrinterProviderMapper;
import com.artiexh.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final UserMapper userMapper;
	private final ArtistMapper artistMapper;
	private final AccountMapper accountMapper;
	private final PrinterProviderMapper printerProviderMapper;

	@Override
	public Account getUserById(Long id) {
		return accountRepository.findById(id)
			.map(accountEntity -> switch (Role.fromValue(accountEntity.getRole())) {
				case ADMIN -> accountMapper.entityToDomain(accountEntity);
				case USER -> userMapper.entityToDomain((UserEntity) accountEntity);
				case ARTIST -> artistMapper.entityToDomain((ArtistEntity) accountEntity);
				case PRINTER_PROVIDER -> printerProviderMapper.entityToDomain((PrinterProviderEntity) accountEntity);
			})
			.orElse(null);
	}

}
