package com.artiexh.api.service.product;

import com.artiexh.data.jpa.entity.ProductInCampaignEntity;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;

public interface ProductService {
	Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable);

	Page<Product> getInPage(Query query, Pageable pageable);

	Product getDetail(long id);

	Product create(long artistId, Product product, ProductInCampaignEntity productInCampaign);

	Product update(long artistId, Product product);

	void delete(long userId, long productId);
}
