package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.CartService;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.mapper.CartMapper;
import com.artiexh.model.rest.cart.request.UpdateCartItemRequest;
import com.artiexh.model.rest.cart.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Cart.ROOT)
public class CartController {

	private final CartService cartService;
	private final CartMapper cartMapper;

	@GetMapping
	public CartResponse getCart(Authentication authentication) {
		long userId = (Long) authentication.getPrincipal();
		Cart cart = cartService.getCart(userId);
		return cartMapper.domainToCartResponse(cart);
	}

	@PutMapping(Endpoint.Cart.ITEM)
	public CartResponse addItemToCart(Authentication authentication, @RequestBody @Validated UpdateCartItemRequest request) {
		long userId = (Long) authentication.getPrincipal();
		cartService.addItemToCart(userId, request.getItems());
		Cart cart = cartService.getCart(userId);
		return cartMapper.domainToCartResponse(cart);
	}

}
