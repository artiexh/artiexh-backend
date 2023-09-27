package com.artiexh.api.controller.provider;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.provider.ProviderService;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.mapper.ProviderMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import com.artiexh.model.rest.provider.filter.ProviderFilter;
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

	//Create Provider
	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProviderDetail create(@Valid @RequestBody ProviderDetail detail) {
		try {
			Provider provider = providerMapper.detailToDomain(detail);
			provider = providerService.create(provider);
			return providerMapper.domainToDetail(provider);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				exception.getMessage(),
				exception);
		}
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

	@PutMapping(Endpoint.Provider.DETAIL)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProviderDetail update(
		@Valid @RequestBody ProviderDetail detail,
		@PathVariable("id") String businessCode) {
		try {
			Provider provider = providerMapper.detailToDomain(detail);
			provider.setBusinessCode(businessCode);
			provider = providerService.update(provider);
			return providerMapper.domainToDetail(provider);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				ErrorCode.PROVIDER_NOT_FOUND.getMessage(),
				exception);
		}
	}
}
