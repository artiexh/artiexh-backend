package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.model.domain.ProvidedProductBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProvidedProductBaseService {
	ProvidedProductBase create(ProvidedProductBase product);
	ProvidedProductBase update(ProvidedProductBase product);
	void delete(String businessCode, Long productBaseId);
	ProvidedProductBase getById(String businessCode, Long productBaseId);
	Page<ProvidedProductBase> getAll(Specification<ProductVariantEntity> specification, Pageable pageable);
}
