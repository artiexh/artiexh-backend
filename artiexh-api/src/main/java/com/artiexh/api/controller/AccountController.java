package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.AccountService;
import com.artiexh.model.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Account.ROOT)
public class AccountController {

	private final AccountService accountService;

	@GetMapping(Endpoint.Account.ME)
	public Account getMe(Authentication authentication) {
		Long id = (Long) authentication.getPrincipal();
		return accountService.getUserById(id);
	}

}
