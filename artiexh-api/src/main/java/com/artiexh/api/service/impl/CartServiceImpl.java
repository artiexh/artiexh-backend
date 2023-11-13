package com.artiexh.api.service.impl;

import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.service.CartService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.CartItemRepository;
import com.artiexh.data.jpa.repository.CartRepository;
import com.artiexh.data.jpa.repository.ProductInventoryRepository;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.mapper.CartMapper;
import com.artiexh.model.rest.marketplace.cart.request.CartItemRequest;
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
	private final ProductRepository productRepository;
	private final ProductInventoryRepository productInventoryRepository;
	private final CartMapper cartMapper;

	@Override
	@Transactional
	public Cart getCart(long userId) {
		CartEntity cartEntity = getOrCreateCartEntity(userId);
		return cartMapper.entityToDomain(cartEntity);
	}

	@Override
	@Transactional
	public Cart updateCartItem(long userId, Set<CartItemRequest> items) {
		CartEntity cartEntity = getOrCreateCartEntity(userId);

		if (items.isEmpty()) {
			cartEntity.getCartItems().clear();
			return cartMapper.entityToDomain(cartRepository.save(cartEntity));
		}

		int numOfItemsBelongToUser = productInventoryRepository.countAllByProductCodeInAndOwnerId(
			items.stream().map(CartItemRequest::getProductCode).toList(),
			userId
		);
		if (numOfItemsBelongToUser > 0) {
			throw new IllegalArgumentException(ErrorCode.INVALID_ITEM.getMessage());
		}

		Set<CartItemEntity> cartItemEntities = items.stream().map(cartItemRequest -> {
			CartItemId cartItemId = new CartItemId(
				cartEntity.getId(),
				cartItemRequest.getSaleCampaignId(),
				cartItemRequest.getProductCode()
			);
			ProductEntity productEntity = ProductEntity.builder()
				.id(new ProductEntityId(
					cartItemRequest.getProductCode(),
					cartItemRequest.getSaleCampaignId())
				)
				.build();
			return CartItemEntity.builder()
				.id(cartItemId)
				.product(productEntity)
				.quantity(cartItemRequest.getQuantity())
				.build();
		}).collect(Collectors.toSet());

		cartEntity.getCartItems().clear();
		cartEntity.getCartItems().addAll(cartItemEntities);
		return cartMapper.entityToDomain(cartRepository.save(cartEntity));
	}

	//	@Override
//	@Transactional
//	public void deleteItemToCart(long userId, Set<Long> items) {
//		if (items.isEmpty()) {
//			return;
//		}
//
//		CartEntity cartEntity = getOrCreateCartEntity(userId);
//		Set<CartItemEntity> cartItemEntities = items.stream().map(productId -> {
//			CartItemId cartItemId = new CartItemId(cartEntity.getId(), productId);
//			return CartItemEntity.builder().id(cartItemId).build();
//		}).collect(Collectors.toSet());
//
//		cartItemRepository.deleteAll(cartItemEntities);
//	}
//
	private CartEntity getOrCreateCartEntity(long userId) {
		return cartRepository.findById(userId)
			.orElseGet(() -> {
				CartEntity newCartEntity = CartEntity.builder().id(userId).build();
				return cartRepository.save(newCartEntity);
			});
	}

}
