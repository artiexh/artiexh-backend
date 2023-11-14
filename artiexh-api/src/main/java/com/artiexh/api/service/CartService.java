package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.CartItemId;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.rest.marketplace.cart.request.CartItemRequest;

import java.util.Set;

public interface CartService {

	Cart getCart(long userId);

	Cart updateCartItem(long userId, Set<CartItemRequest> items);

	void deleteItemToCart(Set<CartItemId> itemIds);

}
