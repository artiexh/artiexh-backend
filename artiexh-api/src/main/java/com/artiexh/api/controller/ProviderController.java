package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.ProvidedProductBaseService;
import com.artiexh.api.service.ProviderService;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import com.artiexh.model.domain.ProductBase;
import com.artiexh.model.domain.ProvidedProductBase;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.mapper.ProvidedProductBaseMapper;
import com.artiexh.model.mapper.ProviderMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.productbase.ProductBaseDetail;
import com.artiexh.model.rest.provider.ProvidedProductBaseDetail;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.Provider.ROOT)
public class ProviderController {
	private final ProviderService providerService;
	private final ProviderMapper providerMapper;
	private final ProvidedProductBaseService providedProductBaseService;
	private final ProvidedProductBaseMapper providedProductBaseMapper;
	//Create Provider
	@PostMapping
	public ProviderDetail create(@Valid @RequestBody ProviderDetail detail) {
		Provider provider = providerMapper.detailToDomain(detail);
		provider = providerService.create(provider);
		return providerMapper.domainToDetail(provider);
	}
	//Get Provider Detail
	@GetMapping
	public PageResponse<ProviderInfo> getInPage(@ParameterObject PaginationAndSortingRequest paginationAndSortingRequest) {
		Page<Provider> providers = providerService.getInPage(paginationAndSortingRequest.getPageable());
		return new PageResponse<>(providers.map(providerMapper::domainToInfo));
	}
	//Create Provided Product
	@PostMapping(Endpoint.Provider.PROVIDED_PRODUCT)
	public ProvidedProductBaseDetail createProvidedProduct(
		@PathVariable("providerId") String businessCode,
		@PathVariable("productBaseId") Long productBaseId,
		@Valid @RequestBody ProvidedProductBaseDetail detail) {
		ProvidedProductBase providedProduct = providedProductBaseMapper.detailToDomain(detail);
		providedProduct.setId(ProvidedProductBaseId.builder()
			.productBaseId(productBaseId)
			.businessCode(businessCode)
			.build());
		providedProduct = providedProductBaseService.create(providedProduct);
		return providedProductBaseMapper.domainToDetail(providedProduct);
	}
	//Update Provided Product
	@PutMapping(Endpoint.Provider.PROVIDED_PRODUCT)
	public ProvidedProductBaseDetail updateProvidedProduct(
		@PathVariable("providerId") String businessCode,
		@PathVariable("productBaseId") Long productBaseId,
		@Valid @RequestBody ProvidedProductBaseDetail detail) {
		ProvidedProductBase providedProduct = providedProductBaseMapper.detailToDomain(detail);
		providedProduct.setId(ProvidedProductBaseId.builder()
			.productBaseId(productBaseId)
			.businessCode(businessCode)
			.build());
		providedProduct = providedProductBaseService.update(providedProduct);
		return providedProductBaseMapper.domainToDetail(providedProduct);
	}
	//Remove Provided Product
	@DeleteMapping(Endpoint.Provider.PROVIDED_PRODUCT)
	public void deleteProvidedProduct(
		@PathVariable("providerId") String businessCode,
		@PathVariable("productBaseId") Long productBaseId) {
		providedProductBaseService.delete(businessCode, productBaseId);
	}
}
