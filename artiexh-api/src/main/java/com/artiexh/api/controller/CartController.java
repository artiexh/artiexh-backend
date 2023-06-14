package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.CartService;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.mapper.CartMapper;
import com.artiexh.model.rest.cart.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
