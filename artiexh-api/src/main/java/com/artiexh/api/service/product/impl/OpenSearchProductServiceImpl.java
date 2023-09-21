package com.artiexh.api.service.product.impl;

import com.artiexh.api.service.product.OpenSearchProductService;
import com.artiexh.data.opensearch.model.ProductDocument;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductSuggestion;
import com.artiexh.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenSearchProductServiceImpl implements OpenSearchProductService {
	private final ElasticsearchOperations openSearchTemplate;
	private final ProductMapper productMapper;

	@Override
	public Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable) {
		query.setPageable(pageable);
		SearchHits<ProductDocument> hits = openSearchTemplate.search(query, ProductDocument.class);
		SearchPage<ProductDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		return hitPage.map(searchHit -> ProductSuggestion.builder().name(searchHit.getContent().getName()).build());
	}

	@Override
	public Page<Product> getInPage(Query query, Pageable pageable) {
		query.setPageable(pageable);
		SearchHits<ProductDocument> hits = openSearchTemplate.search(query, ProductDocument.class);
		SearchPage<ProductDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		return hitPage.map(searchHit -> productMapper.documentToDomain(searchHit.getContent()));
	}

	@Async
	@Override
	public void save(Product product) {
		ProductDocument productDocument = productMapper.domainToDocument(product);
		openSearchTemplate.save(productDocument);
	}

	@Async
	@Override
	public void save(ProductDocument productDocument) {
		openSearchTemplate.save(productDocument);
	}

	@Async
	@Override
	public void update(Product product) {
		ProductDocument productDocument = productMapper.domainToDocument(product);
		openSearchTemplate.update(productDocument);
	}

	@Async
	@Override
	public void delete(long productId) {
		openSearchTemplate.delete(String.valueOf(productId), ProductDocument.class);
	}

}
