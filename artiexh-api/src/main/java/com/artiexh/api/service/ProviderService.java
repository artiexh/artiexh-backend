package com.artiexh.api.service;

import com.artiexh.model.domain.Provider;
import com.artiexh.model.rest.provider.ProviderDetail;

public interface ProviderService {
	Provider create(Provider providerDetail);
	Provider getById(Long providerId);
}
