package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.AccountService;
import com.artiexh.model.domain.Account;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.account.AccountFilter;
import com.artiexh.model.rest.account.AccountProfile;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
		try {
			return accountService.getUserProfile(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage(), ex);
		}
	}

	@PutMapping("/profile")
	public AccountProfile updateProfile(
		Authentication authentication,
		@RequestBody @Valid AccountProfile accountProfile) {
		try {
			Long id = (Long) authentication.getPrincipal();
			return accountService.updateProfile(id, accountProfile);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage(), ex);
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyAuthority('STAFF','ADMIN')")
	public PageResponse<Account> getAll(
		@ParameterObject @Valid PaginationAndSortingRequest pagination,
		@ParameterObject @Valid AccountFilter accountFilter
	) {
		return new PageResponse<>(accountService.getAll(accountFilter.getSpecification(), pagination.getPageable()));
	}
}
