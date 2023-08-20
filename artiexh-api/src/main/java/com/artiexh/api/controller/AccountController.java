package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.AccountService;
import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.Role;
import com.artiexh.model.rest.account.AccountProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

	@GetMapping(value = Endpoint.Account.PROFILE)
	public AccountProfile getProfile(@PathVariable Long id) {
		return accountService.getUserProfile(id);
	}

}
