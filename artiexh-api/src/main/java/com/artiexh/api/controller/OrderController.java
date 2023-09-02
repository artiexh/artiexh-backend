package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.common.Endpoint.Order;
import com.artiexh.api.service.OrderService;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import com.artiexh.model.rest.user.UserOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Order.ROOT)
public class OrderController {

	private final OrderService orderService;
	private final OrderMapper orderMapper;

	@PostMapping(Endpoint.Order.CHECKOUT)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public List<UserOrderResponse> checkout(Authentication authentication,
											@RequestBody @Valid CheckoutRequest request) {
		var userId = (Long) authentication.getPrincipal();
		try {

			return orderService.checkout(userId, request).stream()
				.map(orderMapper::orderToUserResponse)
				.toList();
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	@GetMapping(Order.SHIPPING_FEE)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public ShipFeeResponse.ShipFee getShippingFee(Authentication authentication,
												  @RequestBody @Valid GetShippingFeeRequest request) {
		var userId = (Long) authentication.getPrincipal();
		try {
			return orderService.getShippingFee(userId, request.getAddressId(), request.getShopId(), request.getTotalWeight());
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

}
