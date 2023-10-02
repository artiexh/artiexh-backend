package com.artiexh.api.service.provider;

import com.artiexh.data.jpa.entity.ProductVariantProviderEntity;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.rest.provider.ProviderConfigResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProviderService {
	Provider create(Provider provider);

	Provider update(Provider provider);

	Provider getById(String businessCode);

	Page<Provider> getInPage(Pageable pageable);

	Page<Provider> getInPage(Specification<ProviderEntity> specification, Pageable pageable);

	Page<ProviderConfigResponse> getAllConfig(Specification<ProductVariantProviderEntity> specification, Pageable pageable);
}
