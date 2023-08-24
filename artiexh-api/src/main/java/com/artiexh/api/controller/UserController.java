package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.UserAddressService;
import com.artiexh.api.service.UserService;
import com.artiexh.model.domain.UserAddress;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.order.request.OrderPageFilter;
import com.artiexh.model.rest.user.UserOrderResponsePage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.User.ROOT)
public class UserController {
	private final UserService userService;
	private final UserAddressService userAddressService;

	@GetMapping(Endpoint.User.ADDRESS)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public PageResponse<UserAddress> getAllUserAddress(Authentication authentication,
													   @ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest) {
		long userId = (long) authentication.getPrincipal();
		Page<UserAddress> userAddresses = userAddressService.getByUserId(userId, paginationAndSortingRequest.getPageable());
		return new PageResponse<>(userAddresses);
	}

	@GetMapping(Endpoint.User.ADDRESS + "/{id}")
	public UserAddress getUserAddress(Authentication authentication, @PathVariable Long id) {
		long userId = (long) authentication.getPrincipal();
		try {
			return userAddressService.getById(userId, id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@PostMapping(Endpoint.User.ADDRESS)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserAddress createUserAddress(Authentication authentication,
										 @RequestBody @Valid UserAddress userAddress) {
		long userId = (long) authentication.getPrincipal();
		return userAddressService.create(userId, userAddress);
	}

	@PutMapping(Endpoint.User.ADDRESS + "/{id}")
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserAddress updateUserAddress(Authentication authentication,
										 @PathVariable Long id,
										 @RequestBody @Valid UserAddress userAddress) {
		long userId = (long) authentication.getPrincipal();
		userAddress.setId(id);
		try {
			return userAddressService.update(userId, userAddress);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		} catch (AccessDeniedException ex) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@DeleteMapping(Endpoint.User.ADDRESS + "/{id}")
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserAddress deleteUserAddress(Authentication authentication, @PathVariable Long id) {
		long userId = (long) authentication.getPrincipal();
		try {
			return userAddressService.delete(userId, id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		} catch (AccessDeniedException ex) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@GetMapping(Endpoint.User.ORDER)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public PageResponse<UserOrderResponsePage> getAllOrder(
		Authentication authentication,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject @Valid OrderPageFilter filter
	) {
		long userId = (long) authentication.getPrincipal();
		return userService.getOrderInPage(filter.getSpecificationForUser(userId), paginationAndSortingRequest.getPageable());
	}

}
