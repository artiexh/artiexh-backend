package com.artiexh.api.service;

import com.artiexh.model.domain.ProductInCampaign;

public interface ConfigService {
	//void syncProductToOpenSearch();
	void createCampaignProduct(ProductInCampaign productInCampaign);

	//void syncProductToOpenSearch(Long productId);
}
