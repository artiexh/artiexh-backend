package com.artiexh.api.service.impl;

import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.service.CartService;
import com.artiexh.data.jpa.entity.CartEntity;
import com.artiexh.data.jpa.entity.CartItemEntity;
import com.artiexh.data.jpa.entity.CartItemId;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.repository.CartItemRepository;
import com.artiexh.data.jpa.repository.CartRepository;
import com.artiexh.data.jpa.repository.ProductRepository;
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
	private final ProductRepository productRepository;

	@Override
	public Cart getCart(long userId) {
		CartEntity cartEntity = getOrCreateCartEntity(userId);
		return cartMapper.entityToDomain(cartEntity);
	}

	@Override
	@Transactional
	public void addItemToCart(long userId, Set<CartItemRequest> items) {
		if (items.isEmpty()) {
			return;
		}

		int numOfItemsBelongToUser = productRepository.countAllByIdInAndOwnerId(
			items.stream().map(CartItemRequest::getProductId).toList(),
			userId);
		if (numOfItemsBelongToUser > 0) {
			throw new IllegalArgumentException(ErrorCode.INVALID_ITEM.getMessage());
		}

		CartEntity cartEntity = getOrCreateCartEntity(userId);
		Set<CartItemEntity> cartItemEntities = items.stream().map(cartItemRequest -> {
			CartItemId cartItemId = new CartItemId(cartEntity.getId(), cartItemRequest.getProductId());
			ProductEntity productEntity = ProductEntity.builder().id(cartItemRequest.getProductId()).build();
			return CartItemEntity.builder().id(cartItemId).product(productEntity).quantity(cartItemRequest.getQuantity()).build();
		}).collect(Collectors.toSet());

		cartItemRepository.saveAll(cartItemEntities);
	}

	@Override
	@Transactional
	public void deleteItemToCart(long userId, Set<Long> items) {
		if (items.isEmpty()) {
			return;
		}

		CartEntity cartEntity = getOrCreateCartEntity(userId);
		Set<CartItemEntity> cartItemEntities = items.stream().map(productId -> {
			CartItemId cartItemId = new CartItemId(cartEntity.getId(), productId);
			return CartItemEntity.builder().id(cartItemId).build();
		}).collect(Collectors.toSet());

		cartItemRepository.deleteAll(cartItemEntities);
	}

	@Transactional
	public CartEntity getOrCreateCartEntity(long userId) {
		return cartRepository.findById(userId)
			.orElseGet(() -> {
				CartEntity newCartEntity = CartEntity.builder().id(userId).build();
				return cartRepository.save(newCartEntity);
			});
	}

}
