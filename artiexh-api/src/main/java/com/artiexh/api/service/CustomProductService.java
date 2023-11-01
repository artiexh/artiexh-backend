package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.model.rest.customproduct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomProductService {
	CustomProductGeneralResponse saveGeneral(CustomProductGeneralRequest item);

	CustomProductDesignResponse saveDesign(CustomProductDesignRequest item);

	Page<CustomProductResponse> getAll(Specification<CustomProductEntity> specification, Pageable pageable);

	CustomProductGeneralResponse getGeneralById(Long userId, Long id);

	CustomProductDesignResponse getDesignById(Long userId, Long id);

	void delete(Long userId, Long id);
}
