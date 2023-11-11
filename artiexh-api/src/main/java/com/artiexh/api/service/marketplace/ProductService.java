package com.artiexh.api.service.marketplace;

import com.artiexh.data.jpa.entity.ProductEntity;

public interface ProductService {
	ProductEntity create(ProductEntity entity);
//	Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable);
//
//	Page<Product> getInPage(Query query, Pageable pageable);
//
//	Product getDetail(long id);
//
//	Product create(long artistId, Product product, ProductInCampaignEntity productInCampaign);
//
//	Product update(long artistId, Product product);
//
//	void delete(long userId, long productId);
}
