package com.artiexh.api.service.impl;

import com.artiexh.api.service.UserAddressService;
import com.artiexh.data.jpa.entity.UserAddressEntity;
import com.artiexh.data.jpa.entity.UserEntity;
import com.artiexh.data.jpa.repository.UserAddressRepository;
import com.artiexh.model.domain.UserAddress;
import com.artiexh.model.mapper.UserAddressMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserAddressServiceImpl implements UserAddressService {
	private final UserAddressRepository userAddressRepository;
	private final UserAddressMapper userAddressMapper;

	@Override
	public Page<UserAddress> getByUserId(Long userId, Pageable pageable) {
		return userAddressRepository.findByUserId(userId, pageable)
			.map(userAddressMapper::entityToDomain);
	}

	@Override
	public UserAddress getById(Long userId, Long id) {
		return userAddressRepository.findByIdAndUserId(id, userId)
			.map(userAddressMapper::entityToDomain)
			.orElseThrow(() -> new EntityNotFoundException("Address not existed"));
	}

	@Transactional
	@Override
	public UserAddress create(Long userId, UserAddress userAddress) {
		if (!userAddressRepository.existsByUserId(userId)) {
			userAddress.setIsDefault(true);
		} else if (userAddress.getIsDefault()) {
			userAddressRepository.setAllNotDefaultByUserId(userId);
		}

		UserAddressEntity entity = userAddressMapper.domainToEntity(userAddress);
		entity.setUser(UserEntity.builder().id(userId).build());
		userAddressRepository.save(entity);

		return userAddress;
	}

	@Transactional
	@Override
	public UserAddress update(Long userId, UserAddress userAddress) {
		var oldEntity = userAddressRepository.findById(userAddress.getId())
			.orElseThrow(() -> new EntityNotFoundException("Address not existed"));

		if (!userId.equals(oldEntity.getUser().getId())) {
			throw new AccessDeniedException("User not own this address");
		}

		if (oldEntity.getIsDefault() && !userAddress.getIsDefault()) {
			throw new IllegalArgumentException("Cannot make default address become non default");
		}

		if (userAddress.getIsDefault()) {
			userAddressRepository.setAllNotDefaultByUserId(userId);
		}

		var newEntity = userAddressMapper.domainToEntity(userAddress);
		newEntity.setUser(oldEntity.getUser());
		return userAddressMapper.entityToDomain(userAddressRepository.save(newEntity));
	}

	@Transactional
	@Override
	public UserAddress delete(Long userId, Long id) {
		var entity = userAddressRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Address not existed"));

		if (!userId.equals(entity.getUser().getId())) {
			throw new AccessDeniedException("User not own this address");
		}

		if (entity.getIsDefault()) {
			throw new IllegalArgumentException("Cannot delete default address");
		}

		userAddressRepository.delete(entity);
		return userAddressMapper.entityToDomain(entity);
	}

}
