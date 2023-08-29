package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProvidedProductBaseService;
import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import com.artiexh.data.jpa.repository.ProductBaseRepository;
import com.artiexh.data.jpa.repository.ProvidedProductBaseRepository;
import com.artiexh.data.jpa.repository.ProviderRepository;
import com.artiexh.model.domain.ProvidedProductBase;
import com.artiexh.model.mapper.ProvidedProductBaseMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProvidedProductBaseServiceImpl implements ProvidedProductBaseService {
	private final ProvidedProductBaseRepository repository;
	private final ProvidedProductBaseMapper mapper;
	private final ProviderRepository providerRepository;
	private final ProductBaseRepository productBaseRepository;
	@Override
	public ProvidedProductBase create(ProvidedProductBase product) {
		ProvidedProductBaseEntity entity = mapper.domainToEntity(product);

		entity.setProvider(providerRepository.getReferenceById(product.getId().getBusinessCode()));
		entity.setProductBase(productBaseRepository.getReferenceById(product.getId().getProductBaseId()));

		repository.save(entity);
		return product;
	}

	@Override
	public ProvidedProductBase update(ProvidedProductBase product) {
		repository.findById(product.getId()).orElseThrow(EntityNotFoundException::new);
		ProvidedProductBaseEntity entity = mapper.domainToEntity(product);
		repository.save(entity);
		return product;
	}

	@Override
	public void delete(String businessCode, Long productBaseId) {
		repository.deleteById(ProvidedProductBaseId.builder()
			.businessCode(businessCode)
			.productBaseId(productBaseId)
			.build());
	}
}
