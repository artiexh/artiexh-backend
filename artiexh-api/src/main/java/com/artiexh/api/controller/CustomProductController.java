package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.CustomProductService;
import com.artiexh.model.mapper.CustomProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.customproduct.*;
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

	@PostMapping("/general")
	@PreAuthorize("hasAuthority('ARTIST')")
	public CustomProductGeneralResponse createGeneralItem(Authentication authentication,
														  @Valid @RequestBody CustomProductGeneralRequest detail) {
		long userId = (long) authentication.getPrincipal();
		detail.setArtistId(userId);

		try {
			return customProductService.createGeneral(detail);
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

	@PutMapping(Endpoint.CustomProduct.DETAIL + "/general")
	@PreAuthorize("hasAuthority('ARTIST')")
	public CustomProductGeneralResponse updateGeneralItem(Authentication authentication,
														  @PathVariable long id,
														  @Valid @RequestBody CustomProductGeneralRequest detail) {
		long userId = (long) authentication.getPrincipal();
		detail.setId(id);
		detail.setArtistId(userId);

		try {
			return customProductService.updateGeneral(detail);
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

	@PostMapping("/design")
	@PreAuthorize("hasAuthority('ARTIST')")
	public CustomProductDesignResponse createDesignItem(Authentication authentication,
														@Valid @RequestBody CustomProductDesignRequest detail) {
		long userId = (long) authentication.getPrincipal();
		detail.setArtistId(userId);

		try {
			return customProductService.createDesign(detail);
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

	@PutMapping(Endpoint.CustomProduct.DETAIL + "/design")
	@PreAuthorize("hasAuthority('ARTIST')")
	public CustomProductDesignResponse updateDesignItem(Authentication authentication,
														@PathVariable long id,
														@Valid @RequestBody CustomProductDesignRequest detail) {
		long userId = (long) authentication.getPrincipal();
		detail.setId(id);
		detail.setArtistId(userId);

		try {
			return customProductService.updateDesign(detail);
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
	public PageResponse<CustomProductResponse> getAll(
		Authentication authentication,
		@ParameterObject ItemFilter filter,
		@Valid @ParameterObject PaginationAndSortingRequest paginationAndSortingRequest) {
		try {
			long userId = (long) authentication.getPrincipal();
			filter.setArtistId(userId);
			Page<CustomProductResponse> itemPage = customProductService.getAll(filter.getSpecification(), paginationAndSortingRequest.getPageable());

			return new PageResponse<>(itemPage);
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

	@GetMapping(Endpoint.CustomProduct.DETAIL + "/general")
	@PreAuthorize("hasAuthority('ARTIST')")
	public CustomProductGeneralResponse getGeneralById(Authentication authentication, @PathVariable("id") Long id) {
		long userId = (long) authentication.getPrincipal();
		try {
			return customProductService.getGeneralById(userId, id);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				ErrorCode.PRODUCT_NOT_FOUND.getMessage(),
				exception);
		}
	}

	@GetMapping(Endpoint.CustomProduct.DETAIL + "/design")
	@PreAuthorize("hasAuthority('ARTIST')")
	public CustomProductDesignResponse getDesignById(
		Authentication authentication,
		@PathVariable("id") Long id) {
		try {
			long userId = (long) authentication.getPrincipal();
			return customProductService.getDesignById(userId, id);
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
