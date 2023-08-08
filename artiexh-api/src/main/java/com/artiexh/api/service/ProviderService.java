package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.providedproduct.ProvidedModelDetail;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProviderService {
	ProviderDetail create(ProviderDetail detail);

	ProvidedModelDetail createProvidedModel(ProvidedModelDetail providedProductDetail);

	void removeProvidedProduct(String businessCode, long baseModelId);

	ProvidedModelDetail updateProvidedProduct(ProvidedModelDetail providedProductDetail);

	ProviderDetail update(ProviderDetail detail);

	PageResponse<ProviderInfo> getInPage(Specification<ProviderEntity> specification, Pageable pageable);

	ProviderDetail getDetail(String providerId);
}
