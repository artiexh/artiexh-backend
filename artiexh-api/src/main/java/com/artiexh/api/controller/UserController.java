package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.UserAddressService;
import com.artiexh.api.service.UserService;
import com.artiexh.model.domain.UserAddress;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.order.request.OrderGroupPageFilter;
import com.artiexh.model.rest.order.request.OrderPageFilter;
import com.artiexh.model.rest.user.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

		Sort sort = Sort.by(Sort.Direction.DESC, "isDefault");
		if (paginationAndSortingRequest.getSortBy() != null && !"isDefault".equals(paginationAndSortingRequest.getSortBy())) {
			sort.and(Sort.by(paginationAndSortingRequest.getSortDirection(), paginationAndSortingRequest.getSortBy()));
		}
		Pageable pageable = PageRequest.of(
			paginationAndSortingRequest.getPageNumber() - 1,
			paginationAndSortingRequest.getPageSize(),
			sort
		);

		Page<UserAddress> userAddresses = userAddressService.getByUserId(userId, pageable);
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
										 @RequestBody @Valid UserAddressRequest userAddressRequest) {
		long userId = (long) authentication.getPrincipal();
		return userAddressService.create(userId, userAddressRequest);
	}

	@PutMapping(Endpoint.User.ADDRESS + "/{id}")
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserAddress updateUserAddress(Authentication authentication,
										 @PathVariable Long id,
										 @RequestBody @Valid UserAddressRequest userAddressRequest) {
		long userId = (long) authentication.getPrincipal();
		userAddressRequest.setId(id);
		try {
			return userAddressService.update(userId, userAddressRequest);
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

	@GetMapping(Endpoint.User.ORDER_GROUP)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public PageResponse<UserOrderGroupResponsePage> getAllOrderGroup(
		Authentication authentication,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject @Valid OrderGroupPageFilter filter
	) {
		long userId = (long) authentication.getPrincipal();
		return userService.getOrderGroupInPage(filter.getSpecificationForUser(userId), paginationAndSortingRequest.getPageable());
	}

	@GetMapping(Endpoint.User.ORDER_GROUP + "/{id}")
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserOrderGroupResponse getOrderGroupById(
		@PathVariable Long id,
		Authentication authentication
	) {
		try {
			long userId = (long) authentication.getPrincipal();
			return userService.getOrderGroupById(id, userId);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(ErrorCode.ORDER_NOT_FOUND.getCode(), ErrorCode.ORDER_NOT_FOUND.getMessage(), exception);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
		}
	}

	@GetMapping(Endpoint.User.ORDER_SHOP)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public PageResponse<UserOrderResponsePage> getAllOrder(Authentication authentication,
														   @ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
														   @ParameterObject @Valid OrderPageFilter filter) {
		try {
			long userId = (long) authentication.getPrincipal();
			var userOrdersPage = userService.getOrderInPage(
				filter.getSpecificationForUser(userId),
				paginationAndSortingRequest.getPageable()
			);
			return new PageResponse<>(userOrdersPage);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(ErrorCode.ORDER_NOT_FOUND.getCode(), ErrorCode.ORDER_NOT_FOUND.getMessage(), exception);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
		}
	}

	@GetMapping(Endpoint.User.ORDER_SHOP + "/{id}")
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserOrderResponse getOrderDetail(Authentication authentication,
											@PathVariable Long id) {
		try {
			long userId = (long) authentication.getPrincipal();
			return userService.getOrderById(id, userId);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(ErrorCode.ORDER_IS_INVALID.getCode(), ErrorCode.ORDER_IS_INVALID.getMessage(), exception);
		}
	}

}
