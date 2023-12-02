package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.service.SystemConfigService;
import com.artiexh.api.service.ConfigService;
import com.artiexh.model.domain.ProductInCampaign;
import com.artiexh.model.domain.SystemConfig;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Config.ROOT)
public class ConfigController {
	private final ConfigService configService;
	private final SystemConfigService systemConfigService;

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping(Endpoint.Config.SYNC_PRODUCT_OPEN_SEARCH)
	public void syncProductToOpenSearch() {
		configService.syncProductToOpenSearch();
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping(Endpoint.Config.SYNC_PRODUCT_OPEN_SEARCH + "/campaign-sale/{id}/product-code/{product-code}")
	public void syncProductToOpenSearch(
		@PathVariable("id") Long campaignSaleId,
		@PathVariable("product-code") String productCode
	) {
		configService.syncProductToOpenSearch(productCode, campaignSaleId);
	}

	@PostMapping("/initial-campaign-product")
	public void createCampaignProduct(@RequestBody ProductInCampaign productInCampaign) {
		configService.createCampaignProduct(productInCampaign);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping
	public PageResponse<SystemConfig> getAllSystemConfig(@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
														 @RequestParam(required = false) String keyword) {
		return new PageResponse<>(systemConfigService.getAll(keyword, paginationAndSortingRequest.getPageable()));
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public SystemConfig createSystemConfig(@RequestBody @Valid SystemConfig systemConfig) {

		var entity = systemConfigService.set(systemConfig.getKey(), systemConfig.getValue(), "admin");
		return new SystemConfig(
			entity.getKey(),
			entity.getValue(),
			entity.getUpdatedBy(),
			entity.getModifiedDate(),
			entity.getCreatedDate()
		);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{key}")
	public void deleteSystemConfig(@PathVariable String key) {
		systemConfigService.delete(key);
	}
}
