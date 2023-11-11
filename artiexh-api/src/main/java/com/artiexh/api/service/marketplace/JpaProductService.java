package com.artiexh.api.service.marketplace;

import com.artiexh.data.jpa.entity.ProductEntity;

public interface JpaProductService {
	ProductEntity create(ProductEntity productEntity);
//	Page<Product> fillProductPage(Page<Product> productPage);
//
//	Product getDetail(long id);
//
//	Product create(long artistId, Product product, ProductInCampaignEntity productInCampaign);
//
//	Product update(long artistId, Product product);
//
//	void delete(long userId, long productId);
}
