package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.AccountService;
import com.artiexh.api.service.ArtistService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.artist.filter.ProductPageFilter;
import com.artiexh.model.rest.artist.response.ArtistProfileResponse;
import com.artiexh.model.rest.artist.response.ShopOrderResponse;
import com.artiexh.model.rest.artist.response.ShopOrderResponsePage;
import com.artiexh.model.rest.order.request.OrderPageFilter;
import com.artiexh.model.rest.order.request.UpdateShippingOrderRequest;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Artist.ROOT)
public class ArtistController {

	private final ArtistService artistService;
	private final AccountService accountService;

	@GetMapping(Endpoint.Artist.ARTIST_PROFILE)
	public ArtistProfileResponse getProfile(@PathVariable long id) {
		try {
			return artistService.getProfile(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

	@GetMapping(Endpoint.Artist.ARTIST_PRODUCT)
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN')")
	public PageResponse<ProductResponse> getAllProduct(
		Authentication authentication,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject @Valid ProductPageFilter filter
	) {
		long userId = (long) authentication.getPrincipal();
		filter.setArtistId(userId);
		return artistService.getAllProducts(filter.getQuery(), paginationAndSortingRequest.getPageable());
	}

	@GetMapping(Endpoint.Artist.ARTIST_ORDER)
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN')")
	public PageResponse<ShopOrderResponsePage> getAllOrder(
		Authentication authentication,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject @Valid OrderPageFilter filter
	) {
		long userId = (long) authentication.getPrincipal();
		return artistService.getAllOrder(filter.getSpecificationForArtist(userId), paginationAndSortingRequest.getPageable());
	}

	@GetMapping(Endpoint.Artist.ARTIST_ORDER + "/{id}")
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN')")
	public ShopOrderResponse getOrderById(
		@PathVariable Long id,
		Authentication authentication
	) {
		try {
			long userId = (long) authentication.getPrincipal();
			return artistService.getOrderById(id, userId);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(ErrorCode.ORDER_IS_INVALID.getCode(), ErrorCode.ORDER_IS_INVALID.getMessage(), exception);
		}
	}

	@PutMapping(Endpoint.Artist.ARTIST_ORDER + "/{id}/shipping")
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN')")
	public ShopOrderResponse updateOrderToShippingStatus(
		@PathVariable Long id,
		@RequestBody @Valid UpdateShippingOrderRequest updateShippingOrderRequest,
		Authentication authentication
	) {
		try {
			long userId = (long) authentication.getPrincipal();
			return artistService.updateShippingOrderStatus(userId, id, updateShippingOrderRequest);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.ORDER_NOT_FOUND.getMessage(), exception);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
		}
	}
}
