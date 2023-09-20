package com.artiexh.api.service.product;

import com.artiexh.model.domain.Product;
import org.springframework.data.domain.Page;

public interface JpaProductService {
	Page<Product> fillProductPage(Page<Product> productPage);

	Product getDetail(long id);

	Product create(long artistId, Product product);

	Product update(long artistId, Product product);

	void delete(long userId, long productId);
}
