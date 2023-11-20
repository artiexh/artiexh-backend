package com.artiexh.api.service.marketplace.impl;

import com.artiexh.api.service.marketplace.ProductOpenSearchService;
import com.artiexh.data.opensearch.model.ProductDocument;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductSuggestion;
import com.artiexh.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductOpenSearchServiceImpl implements ProductOpenSearchService {
	private final ElasticsearchOperations openSearchTemplate;
	private final ProductMapper productMapper;

	@Override
	public ProductDocument create(Product product) {
		return openSearchTemplate.save(productMapper.domainToDocument(product));
	}

	@Override
	public UpdateResponse update(Product product) {
		return openSearchTemplate.update(productMapper.domainToDocument(product));
	}

	@Override
	public void delete(Long campaignId, String productCode) {
		String documentId = campaignId + '-' + productCode;
		openSearchTemplate.delete(documentId, ProductDocument.class);
	}

	@Override
	public Page<ProductDocument> getAll(Pageable pageable, Query query) {
		query.setPageable(pageable);
		SearchHits<ProductDocument> hits = openSearchTemplate.search(query, ProductDocument.class);
		SearchPage<ProductDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		return hitPage.map(SearchHit::getContent);
	}

	@Override
	public Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable) {
		query.setPageable(pageable);
		SearchHits<ProductDocument> hits = openSearchTemplate.search(query, ProductDocument.class);
		SearchPage<ProductDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		return hitPage.map(searchHit -> ProductSuggestion.builder().name(searchHit.getContent().getName()).build());
	}

}
