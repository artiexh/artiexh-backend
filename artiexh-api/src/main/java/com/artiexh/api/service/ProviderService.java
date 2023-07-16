package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.providedproduct.ProvidedProductDetail;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public interface ProviderService {
	ProviderDetail create(ProviderDetail detail);
	ProviderDetail createProvidedProductList(String businessCode, Set<ProvidedProductDetail> detail);
	ProviderDetail createProvidedProduct(ProvidedProductDetail providedProductDetail);
	ProviderDetail updateProvidedProduct(ProvidedProductDetail providedProductDetail);
	ProviderDetail update(ProviderDetail detail);
	PageResponse<ProviderInfo> getInPage(Specification<ProviderEntity> specification, Pageable pageable);
	ProviderDetail getDetail(String providerId);
}
