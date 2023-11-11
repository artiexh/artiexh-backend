package com.artiexh.api.service.marketplace;

import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.opensearch.model.ProductInventoryDocument;

public interface OpenSearchProductService {
	ProductInventoryDocument create(ProductEntity entity);
//	Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable);
//
//	Page<Product> getInPage(Query query, Pageable pageable);
//
//	void save(Product product);
//
//	void save(ProductDocument productDocument);
//
//	void update(Product product);
//
//	void delete(long productId);
//
//	boolean prePublishedProduct(Long campaignId);

}
