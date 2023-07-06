package com.artiexh.api.service;

import com.artiexh.model.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;

public interface ProductService {
	Page<Product> getInPage(Query query, Pageable pageable);

	Product getDetail(long id);

	Product create(long artistId, Product product);

	Product update(Product product);

	void delete(long userId, long productId);
}
