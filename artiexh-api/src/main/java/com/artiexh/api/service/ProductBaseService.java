package com.artiexh.api.service;

import com.artiexh.model.domain.ProductBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductBaseService {
	ProductBase create(ProductBase product);

	Page<ProductBase> getInPage(Pageable pageable);

	ProductBase getById(Long id);
}
