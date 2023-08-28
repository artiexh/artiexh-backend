package com.artiexh.api.service;

import com.artiexh.model.domain.Provider;
import com.artiexh.model.rest.provider.ProviderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProviderService {
	Provider create(Provider provider);
	ProviderDetail getById(Long providerId);
	Page<Provider> getInPage(Pageable pageable);
}
