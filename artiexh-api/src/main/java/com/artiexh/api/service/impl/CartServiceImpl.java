package com.artiexh.api.service.impl;

import com.artiexh.api.service.CartService;
import com.artiexh.data.jpa.entity.CartEntity;
import com.artiexh.data.jpa.repository.CartRepository;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartMapper cartMapper;

	@Override
	@Transactional
	public Cart getCart(long userId) {
		CartEntity cartEntity = cartRepository.findById(userId)
			.orElseGet(() -> {
				CartEntity newCartEntity = CartEntity.builder().id(userId).build();
				return cartRepository.save(newCartEntity);
			});
		return cartMapper.entityToDomain(cartEntity);
	}

}
