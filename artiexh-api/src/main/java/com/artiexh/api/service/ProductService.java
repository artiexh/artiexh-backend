package com.artiexh.api.service;

import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.product.ProductDetail;
import com.artiexh.model.rest.product.response.ProductInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;

public interface ProductService {
	ProductDetail getDetail(long id);

	PageResponse<ProductInfoResponse> getInPage(Criteria criteria, Pageable pageable);

	ProductDetail create(ProductDetail merch);

	ProductDetail update(ProductDetail merch);

	void delete(long id);
}
