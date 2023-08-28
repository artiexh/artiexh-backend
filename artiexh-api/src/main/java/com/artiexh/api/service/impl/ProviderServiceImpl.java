package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProviderService;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.rest.provider.ProviderDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {
	@Override
	public Provider create(Provider providerDetail) {
		return null;
	}

	@Override
	public Provider getById(Long providerId) {
		return null;
	}
}
