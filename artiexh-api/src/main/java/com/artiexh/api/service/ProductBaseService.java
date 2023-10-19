package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.ProductBaseEntity;
import com.artiexh.model.domain.ProductBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductBaseService {
	ProductBase create(ProductBase product);

	ProductBase update(ProductBase product);

	Page<ProductBase> getInPage(Specification<ProductBaseEntity> specification, Pageable pageable);

	ProductBase getById(Long id);

	ProductBase updateProductBaseConfig(ProductBase product);
}
