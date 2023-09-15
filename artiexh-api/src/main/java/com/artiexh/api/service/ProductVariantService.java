package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.model.domain.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductVariantService {
	ProductVariant create(ProductVariant product);
	ProductVariant update(ProductVariant product);
	void delete(String businessCode, Long productBaseId);
	ProductVariant getById(Long id);
	Page<ProductVariant> getAll(Specification<ProductVariantEntity> specification, Pageable pageable);
}
