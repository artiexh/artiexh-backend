package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProvidedModelService;
import com.artiexh.api.service.ProviderService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.providedproduct.ProvidedModelDetail;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderFilter;
import com.artiexh.model.rest.provider.ProviderInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.Provider.ROOT)
public class ProviderController {
	private final ProviderService service;
	private final ProvidedModelService providedModelService;

	@PutMapping(path = Endpoint.Provider.PROVIDER_DETAIL)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProviderDetail update(@PathVariable(name = "id") String businessCode, @RequestBody @Valid ProviderDetail detail) {
		detail.setBusinessCode(businessCode);
		try {
			return service.update(detail);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(
				BAD_REQUEST,
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
				OK,
				ErrorCode.PROVIDER_NOT_FOUND.getMessage(),
				exception);
		}

	}

	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProviderDetail create(@RequestBody @Valid ProviderDetail detail) {
		try {
			return service.create(detail);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(
				ErrorCode.PROVIDER_EXISTED.getCode(),
				ErrorCode.PROVIDER_EXISTED.getMessage()
			);
		}

	}

	@PostMapping(path = Endpoint.Provider.PROVIDED_MODEL_DETAIL)
	public ProviderDetail createProvidedModel(
		@PathVariable(name = "providerId") String businessCode,
		@PathVariable long baseModelId,
		@RequestBody @Valid ProvidedModelDetail detail
	) {
		try {
			detail.setBusinessCode(businessCode);
			detail.setBaseModelId(baseModelId);
			return service.createProvidedModel(detail);
		} catch (DataIntegrityViolationException exception) {
			throw new ResponseStatusException(
				ErrorCode.PROVIDED_MODEL_KEY_NOT_VALID.getCode(),
				ErrorCode.PROVIDED_MODEL_KEY_NOT_VALID.getMessage(),
				exception);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(
				ErrorCode.PROVIDED_MODEL_EXISTED.getCode(),
				ErrorCode.PROVIDED_MODEL_EXISTED.getMessage(),
				exception);
		}
	}

	@DeleteMapping(path = Endpoint.Provider.PROVIDED_MODEL_DETAIL)
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteProvidedProduct(
		@PathVariable(name = "providerId") String businessCode,
		@PathVariable long baseModelId
	) {
		try {
			service.removeProvidedProduct(businessCode, baseModelId);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(
				ErrorCode.PROVIDED_MODEL_NOT_FOUND.getCode(),
				ErrorCode.PROVIDED_MODEL_NOT_FOUND.getMessage(),
				exception);
		}
	}

	@PutMapping(path = Endpoint.Provider.PROVIDED_MODEL_DETAIL)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProviderDetail updateProvidedProduct(
		@PathVariable(name = "providerId") String businessCode,
		@PathVariable long baseModelId,
		@RequestBody ProvidedModelDetail detail
	) {
		try {
			detail.setBusinessCode(businessCode);
			detail.setBaseModelId(baseModelId);
			return service.updateProvidedProduct(detail);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(
				ErrorCode.PROVIDED_MODEL_NOT_FOUND.getCode(),
				ErrorCode.PROVIDED_MODEL_NOT_FOUND.getMessage(),
				exception);
		}

	}
}
