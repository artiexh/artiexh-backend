package com.artiexh.api.service;

import com.artiexh.model.domain.UserAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserAddressService {
	Page<UserAddress> getByUserId(Long userId, Pageable pageable);

	UserAddress getById(Long userId, Long id);

	UserAddress create(Long userId, UserAddress userAddress);

	UserAddress update(Long userId, UserAddress userAddress);

	UserAddress delete(Long userId, Long id);
}
