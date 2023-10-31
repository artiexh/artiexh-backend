package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.model.domain.CustomProduct;
import com.artiexh.model.rest.customproduct.CustomProductDesignRequest;
import com.artiexh.model.rest.customproduct.CustomProductDesignResponse;
import com.artiexh.model.rest.customproduct.CustomProductGeneralRequest;
import com.artiexh.model.rest.customproduct.CustomProductGeneralResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomProductService {
	CustomProductGeneralResponse saveGeneral(CustomProductGeneralRequest item);

	CustomProductDesignResponse saveDesign(CustomProductDesignRequest item);

	Page<CustomProduct> getAll(Specification<CustomProductEntity> specification, Pageable pageable);

	CustomProductGeneralResponse getGeneralById(Long userId, Long id);

	CustomProductDesignResponse getDesignById(Long userId, Long id);

	void delete(Long userId, Long id);
}
