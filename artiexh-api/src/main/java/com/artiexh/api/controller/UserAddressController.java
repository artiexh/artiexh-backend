package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.UserAddressService;
import com.artiexh.model.domain.UserAddress;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.User.ROOT + Endpoint.User.ADDRESS)
public class UserAddressController {

	private final UserAddressService userAddressService;

	@GetMapping
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public PageResponse<UserAddress> getAllUserAddress(Authentication authentication,
													   @ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest) {
		long userId = (long) authentication.getPrincipal();
		Page<UserAddress> userAddresses = userAddressService.getByUserId(userId, paginationAndSortingRequest.getPageable());
		return new PageResponse<>(userAddresses);
	}

	@GetMapping("/{id}")
	public UserAddress getUserAddress(Authentication authentication,
									  @PathVariable Long id) {
		long userId = (long) authentication.getPrincipal();
		return userAddressService.getById(userId, id);
	}

	@PostMapping
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserAddress createUserAddress(Authentication authentication, @RequestBody @Valid UserAddress userAddress) {
		long userId = (long) authentication.getPrincipal();
		return userAddressService.create(userId, userAddress);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserAddress updateUserAddress(Authentication authentication,
										 @PathVariable Long id,
										 @RequestBody @Valid UserAddress userAddress) {
		long userId = (long) authentication.getPrincipal();
		userAddress.setId(id);
		return userAddressService.update(userId, userAddress);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserAddress deleteUserAddress(Authentication authentication,
										 @PathVariable Long id) {
		long userId = (long) authentication.getPrincipal();
		return userAddressService.delete(userId, id);
	}

}
