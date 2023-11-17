package com.artiexh.api.service.productinventory.impl;

import com.artiexh.api.service.productinventory.ProductInventoryOpenSearchService;
import com.artiexh.data.opensearch.model.ProductInventoryDocument;
import com.artiexh.model.domain.CampaignSale;
import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.ProductInventory;
import com.artiexh.model.domain.ProductSuggestion;
import com.artiexh.model.mapper.ProductInventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInventoryOpenSearchServiceImpl implements ProductInventoryOpenSearchService {
	private final ElasticsearchOperations openSearchTemplate;
	private final ProductInventoryMapper productInventoryMapper;

	@Override
	public ProductInventoryDocument create(ProductInventory productInventory) {
		return openSearchTemplate.save(productInventoryMapper.domainToDocument(productInventory));
	}

	@Override
	public ProductInventoryDocument update(ProductInventory productInventory) {
		return openSearchTemplate.save(productInventoryMapper.domainToDocument(productInventory));
	}

	@Override
	public ProductInventoryDocument updateSaveCampaign(String productCode, Money price, CampaignSale campaignSale) {
		var document = openSearchTemplate.get(productCode, ProductInventoryDocument.class);
		if (document != null) {
			document.setCampaign(productInventoryMapper.campaignToCampaignDocument(campaignSale));
			document.getPrice().setAmount(price.getAmount());
			document.getPrice().setUnit(price.getUnit());
			openSearchTemplate.save(document);
		}
		return document;
	}

	@Override
	@Async
	public void removeSaveCampaign(String productCode) {
		var document = openSearchTemplate.get(productCode, ProductInventoryDocument.class);
		if (document == null) {
			return;
		}

		document.setCampaign(null);
		document.setPrice(null);

		openSearchTemplate.save(document);
	}

	@Override
	public Page<ProductInventoryDocument> getAll(Pageable pageable, Query query) {
		query.setPageable(pageable);
		SearchHits<ProductInventoryDocument> hits = openSearchTemplate.search(query, ProductInventoryDocument.class);
		SearchPage<ProductInventoryDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		return hitPage.map(SearchHit::getContent);
	}

	@Override
	public Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable) {
		query.setPageable(pageable);
		SearchHits<ProductInventoryDocument> hits = openSearchTemplate.search(query, ProductInventoryDocument.class);
		SearchPage<ProductInventoryDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		return hitPage.map(searchHit -> ProductSuggestion.builder().name(searchHit.getContent().getName()).build());
	}

}
