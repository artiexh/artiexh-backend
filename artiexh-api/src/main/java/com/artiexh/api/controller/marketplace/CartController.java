package com.artiexh.api.controller.marketplace;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.CartService;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.mapper.CartMapper;
import com.artiexh.model.rest.marketplace.cart.request.UpdateCartItemRequest;
import com.artiexh.model.rest.marketplace.cart.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Cart.ROOT)
public class CartController {

	private final CartService cartService;
	private final CartMapper cartMapper;

	@GetMapping
	@PreAuthorize("hasAnyAuthority('USER','ARTIST')")
	public CartResponse getCart(Authentication authentication) {
		long userId = (Long) authentication.getPrincipal();
		Cart cart = cartService.getCart(userId);
		return cartMapper.domainToCartResponse(cart);
	}

	@PutMapping(Endpoint.Cart.ITEM)
	@PreAuthorize("hasAnyAuthority('USER','ARTIST')")
	public CartResponse addItemToCart(Authentication authentication,
									  @RequestBody @Validated UpdateCartItemRequest request) {
		try {
			long userId = (Long) authentication.getPrincipal();
			Cart cart = cartService.updateCartItem(userId, request.getItems());
			return cartMapper.domainToCartResponse(cart);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
		}
	}
//
//	@DeleteMapping(Endpoint.Cart.ITEM)
//	@PreAuthorize("hasAnyAuthority('USER','ARTIST')")
//	public CartResponse deleteCartItem(Authentication authentication,
//									   @RequestBody @Validated DeleteCartItemRequest request) {
//		long userId = (Long) authentication.getPrincipal();
//		cartService.deleteItemToCart(userId, request.getProductIds());
//		Cart cart = cartService.getCart(userId);
//		return cartMapper.domainToCartResponse(cart);
//	}

}
