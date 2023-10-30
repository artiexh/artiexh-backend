package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.model.domain.CustomProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomProductService {
	CustomProduct save(CustomProduct item);

	Page<CustomProduct> getAll(Specification<CustomProductEntity> specification, Pageable pageable);

	CustomProduct getById(Long userId, Long id);

	void delete(Long userId, Long id);
}
