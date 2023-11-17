package com.artiexh.api.service.marketplace;

import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductEntityId;
import com.artiexh.model.domain.Product;
import com.artiexh.model.rest.marketplace.salecampaign.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaProductService {
	Product create(ProductEntity productEntity);

	Product update(ProductEntity productEntity);

	Page<ProductResponse> getByProductInventoryId(Page<ProductEntityId> idPage, Pageable pageable);

	ProductResponse getById(ProductEntityId id);

	void delete(ProductEntity entity);


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
