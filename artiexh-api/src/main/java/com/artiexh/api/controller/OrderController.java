package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Order.ROOT)
public class OrderController {

	@PostMapping(Endpoint.Order.CHECKOUT)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public void checkout(Authentication authentication,
						 @RequestBody @Valid CheckoutRequest request) {
		var userId = (Long) authentication.getPrincipal();


	}

}
