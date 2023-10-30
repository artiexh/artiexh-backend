package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.CustomProductService;
import com.artiexh.model.domain.CustomProduct;
import com.artiexh.model.mapper.CustomProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.inventory.CustomProductDetail;
import com.artiexh.model.rest.inventory.ItemFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.CustomProduct.ROOT)
public class CustomProductController {
	private final CustomProductService customProductService;
	private final CustomProductMapper customProductMapper;

	@PostMapping()
	@PreAuthorize("hasAuthority('ARTIST')")
	public CustomProductDetail saveItem(
		Authentication authentication,
		@Valid @RequestBody CustomProductDetail detail) {
		try {
			long userId = (long) authentication.getPrincipal();
			detail.setArtistId(userId);

			CustomProduct item = customProductMapper.detailToDomain(detail);
			item = customProductService.save(item);

			return customProductMapper.domainToDetail(item);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				exception.getMessage(),
				exception);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				exception.getMessage(),
				exception);
		}
	}

	@GetMapping()
	@PreAuthorize("hasAuthority('ARTIST')")
	public PageResponse<CustomProductDetail> getAll(
		Authentication authentication,
		@ParameterObject ItemFilter filter,
		@Valid @ParameterObject PaginationAndSortingRequest paginationAndSortingRequest) {
		try {
			long userId = (long) authentication.getPrincipal();
			filter.setArtistId(userId);
			Page<CustomProduct> itemPage = customProductService.getAll(filter.getSpecification(), paginationAndSortingRequest.getPageable());

			return new PageResponse<>(itemPage.map(customProductMapper::domainToDetail));
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				ErrorCode.PRODUCT_NOT_FOUND.getMessage(),
				exception);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				exception.getMessage(),
				exception);
		}
	}

	@GetMapping(Endpoint.CustomProduct.DETAIL)
	@PreAuthorize("hasAuthority('ARTIST')")
	public CustomProductDetail getById(
		Authentication authentication,
		@PathVariable("id") Long id) {
		try {
			long userId = (long) authentication.getPrincipal();
			CustomProduct item = customProductService.getById(userId, id);

			return customProductMapper.domainToDetail(item);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				ErrorCode.PRODUCT_NOT_FOUND.getMessage(),
				exception);
		}
	}

	@DeleteMapping(Endpoint.CustomProduct.DETAIL)
	@PreAuthorize("hasAuthority('ARTIST')")
	public void delete(
		Authentication authentication,
		@PathVariable("id") Long id) {
		try {
			long userId = (long) authentication.getPrincipal();
			customProductService.delete(userId, id);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				ErrorCode.PRODUCT_NOT_FOUND.getMessage(),
				exception);
		}
	}
}
