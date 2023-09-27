package com.artiexh.api.service.provider.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.provider.ProviderService;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.data.jpa.repository.ProductBaseRepository;
import com.artiexh.data.jpa.repository.ProviderRepository;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.mapper.CycleAvoidingMappingContext;
import com.artiexh.model.mapper.ProviderMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {
	private final ProviderMapper providerMapper;
	private final ProviderRepository providerRepository;
	private final ProductBaseRepository productBaseRepository;

	@Override
	@Transactional
	public Provider create(Provider provider) {
		providerRepository.findById(provider.getBusinessCode()).ifPresent(entity -> {
			throw new IllegalArgumentException(ErrorCode.PROVIDER_EXISTED.getMessage() + entity.getBusinessCode());
		});
		ProviderEntity entity = providerMapper.domainToEntity(provider);
		providerRepository.save(entity);
		return provider;
	}

	@Override
	@Transactional
	public Provider update(Provider provider) {
		ProviderEntity entity = providerRepository.findById(provider.getBusinessCode()).orElseThrow(() -> {
			throw new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + provider.getBusinessCode());
		});
		entity = providerMapper.domainToEntity(provider, entity);
		providerRepository.save(entity);
		return provider;
	}

	@Override
	@Transactional(readOnly = true)
	public Provider getById(String businessCode) {
		ProviderEntity provider = providerRepository.findById(businessCode).orElseThrow(EntityNotFoundException::new);
		return providerMapper.entityToDomain(provider, new CycleAvoidingMappingContext());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Provider> getInPage(Pageable pageable) {
		Page<ProviderEntity> page = providerRepository.findAll(pageable);
		return page.map(item -> providerMapper.entityToDomain(item, new CycleAvoidingMappingContext()));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Provider> getInPage(Specification<ProviderEntity> specification, Pageable pageable) {
		Page<ProviderEntity> page = providerRepository.findAll(specification, pageable);
		return page.map(item -> providerMapper.entityToDomain(item, new CycleAvoidingMappingContext()));
	}
}
