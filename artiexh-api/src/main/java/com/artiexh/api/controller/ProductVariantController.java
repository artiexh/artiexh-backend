package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProductVariantService;
import com.artiexh.model.domain.ProductVariant;
import com.artiexh.model.mapper.ProductVariantMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.productvariant.ProductVariantDetail;
import com.artiexh.model.rest.productvariant.ProductVariantFilter;
import com.artiexh.model.rest.productvariant.request.UpdateProductVariantDetail;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.ProductVariant.ROOT)
public class ProductVariantController {
	private final ProductVariantService productVariantService;
	private final ProductVariantMapper productVariantMapper;

	@GetMapping()
	public PageResponse<ProductVariantDetail> getAll(
		@ParameterObject PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject ProductVariantFilter filter) {
		Page<ProductVariant> productPage = productVariantService.getAll(filter.getSpecification(), paginationAndSortingRequest.getPageable());
		return new PageResponse<>(productPage.map(productVariantMapper::domainToDetail));
	}

	//Create Provided Product
	@PostMapping()
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProductVariantDetail create(
		@Valid @RequestBody ProductVariantDetail detail) {
		ProductVariant providedProduct = productVariantMapper.detailToDomain(detail);
		try {
			providedProduct = productVariantService.create(providedProduct);
			return productVariantMapper.domainToDetail(providedProduct);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				exception.getMessage(),
				exception);
		}

	}


	//TODO: Update product variant
	@PutMapping(Endpoint.ProductVariant.DETAIL)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProductVariantDetail update(
		@PathVariable("id") Long id,
		@Valid @RequestBody UpdateProductVariantDetail detail) {
		ProductVariant productVariant = productVariantMapper.updateRequestToDomain(detail);
		productVariant.setId(id);
		try {
			productVariant = productVariantService.update(productVariant);
			return productVariantMapper.domainToDetail(productVariant);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				exception.getMessage());
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				exception.getMessage(),
				exception);
		}
	}

//	//TODO: Remove Provided Product
//	@DeleteMapping(Endpoint.Provider.ROOT + Endpoint.ProductVariant.DETAIL)
//	@PreAuthorize("hasAuthority('ADMIN')")
//	public void delete(
//		@PathVariable("providerId") String businessCode,
//		@PathVariable("productBaseId") Long productBaseId) {
//		try {
//			productVariantService.delete(businessCode, productBaseId);
//		} catch (EntityNotFoundException exception) {
//			throw new ResponseStatusException(ErrorCode.PRODUCT_NOT_FOUND.getCode(),
//				ErrorCode.PRODUCT_NOT_FOUND.getMessage());
//		}
//	}

	@GetMapping(Endpoint.ProductVariant.DETAIL)
	public ProductVariantDetail getById(
		@PathVariable("id") Long id) {
		try {
			ProductVariant product = productVariantService.getById(id);
			return productVariantMapper.domainToDetail(product);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				ErrorCode.PRODUCT_NOT_FOUND.getMessage(),
				exception);
		}
	}
}
