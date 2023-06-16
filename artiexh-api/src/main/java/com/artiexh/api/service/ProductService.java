package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.rest.product.ProductDetail;
import com.artiexh.model.rest.product.ProductInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {
	ProductDetail getDetail(long id);

	PageResponse<ProductInfo> getInPage(Specification<MerchEntity> specification, Pageable pageable);

	ProductDetail create(ProductDetail merch);

	ProductDetail update(ProductDetail merch);

	void delete(long id);
}
