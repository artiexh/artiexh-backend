package com.artiexh.api.service;

import com.artiexh.model.domain.Cart;
import com.artiexh.model.rest.cart.request.CartItemRequest;

import java.util.Set;

public interface CartService {

	Cart getCart(long userId);

	void addItemToCart(long userId, Set<CartItemRequest> items);

	void deleteItemToCart(long userId, Set<Long> items);

}
