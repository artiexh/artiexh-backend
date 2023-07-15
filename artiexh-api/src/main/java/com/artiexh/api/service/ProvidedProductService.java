package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.data.jpa.entity.ProvidedProductEntity;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import com.artiexh.model.rest.providedproduct.ProvidedProductDetail;
import com.artiexh.model.rest.providedproduct.ProvidedProductInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProvidedProductService {
	ProvidedProductDetail create(ProvidedProductDetail detail);
	ProvidedProductDetail update(ProvidedProductDetail detail);
	PageResponse<ProvidedProductInfo> getInPage(Specification<ProvidedProductEntity> specification, Pageable pageable);
	ProvidedProductDetail getDetail(long baseModelId, String providerId);
}
