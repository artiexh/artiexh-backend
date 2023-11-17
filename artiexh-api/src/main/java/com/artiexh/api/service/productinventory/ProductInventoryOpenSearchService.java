package com.artiexh.api.service.productinventory;

import com.artiexh.data.opensearch.model.ProductInventoryDocument;
import com.artiexh.model.domain.CampaignSale;
import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.ProductInventory;
import com.artiexh.model.domain.ProductSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;

public interface ProductInventoryOpenSearchService {

	ProductInventoryDocument create(ProductInventory productInventory);

	ProductInventoryDocument update(ProductInventory productInventory);

	ProductInventoryDocument updateSaveCampaign(String productCode, Money price, CampaignSale campaignSale);

	ProductInventoryDocument updatePrice(String productCode, Money price);

	void removeSaveCampaign(String productCode);

	Page<ProductInventoryDocument> getAll(Pageable pageable, Query query);

	Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable);
}
