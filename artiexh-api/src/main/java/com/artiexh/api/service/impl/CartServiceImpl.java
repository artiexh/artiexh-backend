package com.artiexh.api.service.impl;

import com.artiexh.api.service.CartService;
import com.artiexh.data.jpa.entity.CartEntity;
import com.artiexh.data.jpa.entity.CartItemEntity;
import com.artiexh.data.jpa.entity.CartItemId;
import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.data.jpa.repository.CartItemRepository;
import com.artiexh.data.jpa.repository.CartRepository;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.mapper.CartMapper;
import com.artiexh.model.rest.cart.request.CartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final CartMapper cartMapper;

	@Override
	@Transactional
	public Cart getCart(long userId) {
		CartEntity cartEntity = getOrCreateCartEntity(userId);
		return cartMapper.entityToDomain(cartEntity);
	}

	@Override
	@Transactional
	public void addItemToCart(long userId, Set<CartItemRequest> items) {
		CartEntity cartEntity = getOrCreateCartEntity(userId);
		Set<CartItemEntity> cartItemEntities = items.stream().map(cartItemRequest -> {
			CartItemId cartItemId = new CartItemId(cartEntity.getId(), cartItemRequest.getProductId());
			MerchEntity merchEntity = MerchEntity.builder().id(cartItemRequest.getProductId()).build();
			return CartItemEntity.builder().id(cartItemId).merch(merchEntity).quantity(cartItemRequest.getQuantity()).build();
		}).collect(Collectors.toSet());

		cartItemRepository.saveAll(cartItemEntities);
	}

	private CartEntity getOrCreateCartEntity(long userId) {
		return cartRepository.findById(userId)
			.orElseGet(() -> {
				CartEntity newCartEntity = CartEntity.builder().id(userId).build();
				return cartRepository.save(newCartEntity);
			});
	}

}
