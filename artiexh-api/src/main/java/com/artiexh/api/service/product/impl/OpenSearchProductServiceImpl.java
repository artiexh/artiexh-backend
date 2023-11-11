package com.artiexh.api.service.product.impl;

import com.artiexh.api.service.product.OpenSearchProductService;
import com.artiexh.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenSearchProductServiceImpl implements OpenSearchProductService {
	private final ElasticsearchOperations openSearchTemplate;
	private final ProductMapper productMapper;

//	@Override
//	public Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable) {
//		query.setPageable(pageable);
//		SearchHits<ProductDocument> hits = openSearchTemplate.search(query, ProductDocument.class);
//		SearchPage<ProductDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
//		return hitPage.map(searchHit -> ProductSuggestion.builder().name(searchHit.getContent().getName()).build());
//	}
//
//	@Override
//	public Page<Product> getInPage(Query query, Pageable pageable) {
//		query.setPageable(pageable);
//		SearchHits<ProductDocument> hits = openSearchTemplate.search(query, ProductDocument.class);
//		SearchPage<ProductDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
//		return hitPage.map(searchHit -> productMapper.documentToDomain(searchHit.getContent()));
//	}
//
//	public boolean prePublishedProduct(Long campaignId) {
//		Query boolQuery = new NativeSearchQueryBuilder()
//			.withQuery(new BoolQueryBuilder().must(new TermQueryBuilder("campaign.id", campaignId)))
//			.build();
//		Map<String, Object> option = new HashMap<>();
//		option.put("campaign.isPrePublished", true);
//		ByQueryResponse response = openSearchTemplate.updateByQuery(UpdateQuery.builder(boolQuery).withParams(option).build(), IndexCoordinates.of("product"));
//        return response.getUpdated() > 0;
//    }
//
//	@Async
//	@Override
//	public void save(Product product) {
//		ProductDocument productDocument = productMapper.domainToDocument(product);
//		openSearchTemplate.save(productDocument);
//	}
//
//	@Async
//	@Override
//	public void save(ProductDocument productDocument) {
//		openSearchTemplate.save(productDocument);
//	}
//
//	@Async
//	@Override
//	public void update(Product product) {
//		ProductDocument productDocument = productMapper.domainToDocument(product);
//		openSearchTemplate.update(productDocument);
//	}
//
//	@Async
//	@Override
//	public void delete(long productId) {
//		openSearchTemplate.delete(String.valueOf(productId), ProductDocument.class);
//	}

}
