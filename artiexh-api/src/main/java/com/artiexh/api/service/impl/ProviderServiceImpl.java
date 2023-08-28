package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProviderService;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.data.jpa.repository.ProviderRepository;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.mapper.ProviderMapper;
import com.artiexh.model.rest.provider.ProviderDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {
	private final ProviderMapper providerMapper;
	private final ProviderRepository providerRepository;
	@Override
	public Provider create(Provider provider) {
		ProviderEntity entity = providerMapper.domainToEntity(provider);
		providerRepository.save(entity);
		return provider;
	}

	@Override
	public ProviderDetail getById(Long providerId) {
		return null;
	}

	@Override
	public Page<Provider> getInPage(Pageable pageable) {
		Page<ProviderEntity> page = providerRepository.findAll(pageable);
		return page.map(providerMapper::entityToDomain);
	}
}
