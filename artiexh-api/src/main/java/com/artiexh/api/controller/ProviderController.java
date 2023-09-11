package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.CollectionService;
import com.artiexh.api.service.ProvidedProductBaseService;
import com.artiexh.api.service.ProviderService;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import com.artiexh.model.domain.Collection;
import com.artiexh.model.domain.ProvidedProductBase;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.mapper.CollectionMapper;
import com.artiexh.model.mapper.ProvidedProductBaseMapper;
import com.artiexh.model.mapper.ProviderMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.provider.*;
import com.artiexh.model.rest.provider.filter.ProviderFilter;
import com.artiexh.model.rest.provider.filter.ProviderProductFilter;
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
@RequestMapping(path = Endpoint.Provider.ROOT)
public class ProviderController {
	private final ProviderService providerService;
	private final ProviderMapper providerMapper;
	private final ProvidedProductBaseService providedProductBaseService;
	private final ProvidedProductBaseMapper providedProductBaseMapper;
	private final CollectionService collectionService;
	private final CollectionMapper collectionMapper;
	//Create Provider
	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProviderDetail create(@Valid @RequestBody ProviderDetail detail) {
		Provider provider = providerMapper.detailToDomain(detail);
		provider = providerService.create(provider);
		return providerMapper.domainToDetail(provider);
	}
	//Get Provider Detail
	@GetMapping
	public PageResponse<ProviderInfo> getInPage(
		@ParameterObject PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject ProviderFilter providerFilter) {
		Page<Provider> providers = providerService.getInPage(providerFilter.getSpecification(), paginationAndSortingRequest.getPageable());
		return new PageResponse<>(providers.map(providerMapper::domainToInfo));
	}

	@GetMapping(Endpoint.Provider.DETAIL)
	public ProviderDetail getById(
		@PathVariable("id") String businessCode) {
		try {
			Provider provider = providerService.getById(businessCode);
			return providerMapper.domainToDetail(provider);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				ErrorCode.PROVIDER_NOT_FOUND.getMessage(),
				exception);
		}
	}

	@GetMapping(Endpoint.Provider.PROVIDER_PRODUCT)
	public PageResponse<ProvidedProductBaseDetail> getProvidedProduct(
		@ParameterObject PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject ProviderProductFilter filter,
		@PathVariable("id") String businessCode) {
		filter.setBusinessCode(businessCode);
		Page<ProvidedProductBase> productPage = providedProductBaseService.getAll(filter.getSpecification(), paginationAndSortingRequest.getPageable());
		return new PageResponse<>(productPage.map(providedProductBaseMapper::domainToDetail));
	}
	//Create Provided Product
	@PostMapping(Endpoint.Provider.PROVIDED_PRODUCT)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProvidedProductBaseDetail createProvidedProduct(
		@PathVariable("providerId") String businessCode,
		@PathVariable("productBaseId") Long productBaseId,
		@Valid @RequestBody ProvidedProductBaseDetail detail) {
		ProvidedProductBase providedProduct = providedProductBaseMapper.detailToDomain(detail);
		providedProduct.setProvidedProductBaseId(ProvidedProductBaseId.builder()
			.productBaseId(productBaseId)
			.businessCode(businessCode)
			.build());
		try {
			providedProduct = providedProductBaseService.create(providedProduct);
			return providedProductBaseMapper.domainToDetail(providedProduct);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				exception.getMessage(),
				exception);
		}

	}


	//Update Provided Product
	@PutMapping(Endpoint.Provider.PROVIDED_PRODUCT)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProvidedProductBaseDetail updateProvidedProduct(
		@PathVariable("providerId") String businessCode,
		@PathVariable("productBaseId") Long productBaseId,
		@Valid @RequestBody ProvidedProductBaseDetail detail) {
		ProvidedProductBase providedProduct = providedProductBaseMapper.detailToDomain(detail);
		providedProduct.setProvidedProductBaseId(ProvidedProductBaseId.builder()
			.productBaseId(productBaseId)
			.businessCode(businessCode)
			.build());
		try {
			providedProduct = providedProductBaseService.update(providedProduct);
			return providedProductBaseMapper.domainToDetail(providedProduct);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(ErrorCode.PRODUCT_NOT_FOUND.getCode(),
				ErrorCode.PRODUCT_NOT_FOUND.getMessage());
		}

	}
	//Remove Provided Product
	@DeleteMapping(Endpoint.Provider.PROVIDED_PRODUCT)
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteProvidedProduct(
		@PathVariable("providerId") String businessCode,
		@PathVariable("productBaseId") Long productBaseId) {
		try {
			providedProductBaseService.delete(businessCode, productBaseId);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(ErrorCode.PRODUCT_NOT_FOUND.getCode(),
				ErrorCode.PRODUCT_NOT_FOUND.getMessage());
		}
	}

	@GetMapping(Endpoint.Provider.PROVIDED_PRODUCT)
	public ProvidedProductBaseDetail getById(
		@PathVariable("providerId") String businessCode,
		@PathVariable("productBaseId") Long productBaseId) {
		try {
			ProvidedProductBase product = providedProductBaseService.getById(businessCode, productBaseId);
			return providedProductBaseMapper.domainToDetail(product);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				ErrorCode.PRODUCT_NOT_FOUND.getMessage(),
				exception);
		}
	}
}
