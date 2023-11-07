package com.artiexh.api.service.product.impl;

import com.artiexh.api.service.product.OpenSearchProductService;
import com.artiexh.data.opensearch.model.ProductDocument;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductSuggestion;
import com.artiexh.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryShardContext;
import org.opensearch.index.query.TermQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

	public boolean prePublishedProduct(Long campaignId) {
		Query boolQuery = new NativeSearchQueryBuilder()
			.withQuery(new BoolQueryBuilder().must(new TermQueryBuilder("campaign.id", campaignId)))
			.build();
		Map<String, Object> option = new HashMap<>();
		option.put("campaign.isPrePublished", true);
		ByQueryResponse response = openSearchTemplate.updateByQuery(UpdateQuery.builder(boolQuery).withParams(option).build(), IndexCoordinates.of("product"));
        return response.getUpdated() > 0;
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
