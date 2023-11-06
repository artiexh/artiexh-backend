package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.ConfigService;
import com.artiexh.model.domain.ProductInCampaign;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Config.ROOT)
public class ConfigController {
	private final ConfigService configService;

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping(Endpoint.Config.SYNC_PRODUCT_OPEN_SEARCH)
	public void syncProductToOpenSearch() {
		configService.syncProductToOpenSearch();
	}

	@PostMapping("/initial-campaign-product")
	public void createCampaignProduct(@RequestBody ProductInCampaign productInCampaign) {
		configService.createCampaignProduct(productInCampaign);
	}

	@PostMapping(Endpoint.Config.SYNC_PRODUCT_OPEN_SEARCH + "/{id}")
	public void syncProductToOpenSearch(@PathVariable Long id) {
		configService.syncProductToOpenSearch(id);
	}

}
