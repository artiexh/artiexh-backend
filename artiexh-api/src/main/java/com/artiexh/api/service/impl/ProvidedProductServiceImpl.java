package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProvidedProductService;
import com.artiexh.data.jpa.entity.ProvidedProductEntity;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.providedproduct.ProvidedProductDetail;
import com.artiexh.model.rest.providedproduct.ProvidedProductInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProvidedProductServiceImpl implements ProvidedProductService {
	@Override
	public ProvidedProductDetail create(ProvidedProductDetail detail) {
		return null;
	}

	@Override
	public ProvidedProductDetail update(ProvidedProductDetail detail) {
		return null;
	}

	@Override
	public PageResponse<ProvidedProductInfo> getInPage(Specification<ProvidedProductEntity> specification, Pageable pageable) {
		return null;
	}

	@Override
	public ProvidedProductDetail getDetail(long baseModelId, String providerId) {
		return null;
	}
}
