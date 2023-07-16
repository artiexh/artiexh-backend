package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProviderService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.providedproduct.ProvidedProductDetail;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderFilter;
import com.artiexh.model.rest.provider.ProviderInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.Provider.ROOT)
public class ProviderController {
	private final ProviderService service;
	@PutMapping(path = Endpoint.Provider.PROVIDER_DETAIL)
	public ProviderDetail update(@PathVariable(name = "id") String businessCode, @RequestBody @Valid ProviderDetail detail) {
		detail.setBusinessCode(businessCode);
		try {
			return service.update(detail);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(
				ErrorCode.PRODUCT_NOT_FOUND.getCode(),
				ErrorCode.PROVIDER_NOT_FOUND.getMessage(),
				exception);
		}
	}

	@GetMapping
	public PageResponse<ProviderInfo> getInPage(
		@ParameterObject @Valid ProviderFilter filter,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest
	) {
		return service.getInPage(filter.getSpecification(), paginationAndSortingRequest.getPageable());
	}

	@GetMapping(path = Endpoint.Provider.PROVIDER_DETAIL)
	public ProviderDetail getDetail(@PathVariable(name = "id") String businessCode) {
		try {
			return service.getDetail(businessCode);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(
				ErrorCode.PROVIDER_NOT_FOUND.getCode(),
				ErrorCode.PROVIDER_NOT_FOUND.getMessage(),
				exception);
		}

	}
	@PostMapping
	public ProviderDetail create(@RequestBody @Valid ProviderDetail detail) {
		return service.create(detail);
	}

	@PostMapping(path = Endpoint.Provider.PROVIDED_PRODUCT_LIST)
	public ProviderDetail createProvidedProductList(
		@PathVariable(name = "providerId") String businessCode,
		@RequestBody @Valid Set<ProvidedProductDetail> detail
	) {
		return service.createProvidedProductList(businessCode, detail);
	}

	@PostMapping(path = Endpoint.Provider.PROVIDED_PRODUCT_DETAIL)
	public ProviderDetail createProvidedProduct(
		@PathVariable(name = "providerId") String businessCode,
		@PathVariable long baseModelId,
		@RequestBody ProvidedProductDetail detail
	) {
		detail.setBusinessCode(businessCode);
		detail.setBaseModelId(baseModelId);
		return service.createProvidedProduct(detail);
	}

	@PutMapping(path = Endpoint.Provider.PROVIDED_PRODUCT_DETAIL)
	public ProviderDetail updateProvidedProduct(
		@PathVariable(name = "providerId") String businessCode,
		@PathVariable long baseModelId,
		@RequestBody ProvidedProductDetail detail
	) {
		detail.setBusinessCode(businessCode);
		detail.setBaseModelId(baseModelId);
		return service.updateProvidedProduct(detail);
	}
}
