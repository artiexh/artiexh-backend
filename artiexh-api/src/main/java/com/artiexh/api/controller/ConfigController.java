package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.base.service.SystemConfigService;
import com.artiexh.api.service.ConfigService;
import com.artiexh.api.service.OrderService;
import com.artiexh.api.service.marketplace.SaleCampaignService;
import com.artiexh.model.domain.ProductInCampaign;
import com.artiexh.model.domain.SystemConfig;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Config.ROOT)
public class ConfigController {
	private final ConfigService configService;
	private final SystemConfigService systemConfigService;
	private final OrderService orderService;
	private final SaleCampaignService saleCampaignService;

	@PostMapping(Endpoint.Config.SYNC_PRODUCT_OPEN_SEARCH)
	public void syncProductToOpenSearch() {
		configService.syncProductToOpenSearch();
	}

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

	@GetMapping
	public PageResponse<SystemConfig> getAllSystemConfig(@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
														 @RequestParam(required = false) String keyword) {
		return new PageResponse<>(systemConfigService.getAll(keyword, paginationAndSortingRequest.getPageable()));
	}

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

	@DeleteMapping("/{key}")
	public void deleteSystemConfig(@PathVariable String key) {
		systemConfigService.delete(key);
	}

	@GetMapping("error-code")
	public List<ErrorCode> getAllErrorCode() {
		return Arrays.stream(ErrorCode.values()).toList();
	}

	@PostMapping("/reload")
	public void reload() {
		systemConfigService.reload();
	}

	@PostMapping("/close-order-timeout")
	public void closeTimeoutOrder() {
		orderService.closedTimeoutOrder();
	}

	@PostMapping("/close-sale-campaign-expire")
	public void closeSaleCampaignExpire() {
		saleCampaignService.closeExpiredSaleCampaigns();
	}
}
