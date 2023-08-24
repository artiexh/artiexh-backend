package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Order.ROOT)
public class OrderController {

	@PostMapping
	public void createOrder(Authentication authentication) {


	}

}
