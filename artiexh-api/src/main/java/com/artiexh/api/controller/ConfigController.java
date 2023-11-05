package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.ConfigService;
import com.artiexh.model.domain.ProductInCampaign;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
