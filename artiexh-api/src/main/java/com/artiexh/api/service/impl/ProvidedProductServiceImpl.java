package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProvidedProductService;
import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.data.jpa.entity.ProvidedProductEntity;
import com.artiexh.data.jpa.entity.ProvidedProductId;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.data.jpa.repository.BaseModelRepository;
import com.artiexh.data.jpa.repository.ProvidedProductRepository;
import com.artiexh.data.jpa.repository.ProviderRepository;
import com.artiexh.model.mapper.ProvidedProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import com.artiexh.model.rest.providedproduct.ProvidedProductDetail;
import com.artiexh.model.rest.providedproduct.ProvidedProductInfo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProvidedProductServiceImpl implements ProvidedProductService {
	private final ProvidedProductRepository providedProductRepository;
	private final ProviderRepository providerRepository;
	private final BaseModelRepository baseModelRepository;
	private final ProvidedProductMapper mapper;
	@Override
	public ProvidedProductDetail create(ProvidedProductDetail detail) {
		ProvidedProductEntity entity = mapper.detailToEntity(detail);
		ProviderEntity provider = providerRepository.getReferenceById(detail.getBusinessCode());
		BaseModelEntity baseModel = baseModelRepository.getReferenceById(detail.getBaseModelId());
		entity.setBaseModel(baseModel);
		entity.setProvider(provider);
		providedProductRepository.saveAndFlush(entity);
		return detail;
	}

	@Override
	public void createList(String businessCode, Set<ProvidedProductDetail> details) {

		for (ProvidedProductDetail detail : details) {
			detail.setBusinessCode(businessCode);
			createOrUpdate(detail);
		}
	}

	private ProvidedProductEntity createOrUpdate(ProvidedProductDetail detail) {
		BaseModelEntity baseModel = baseModelRepository.getReferenceById(detail.getBaseModelId());
		ProviderEntity provider = providerRepository.getReferenceById(detail.getBusinessCode());
		boolean isExisted = providedProductRepository.existsById(
			ProvidedProductId.builder()
				.businessCode(detail.getBusinessCode())
				.baseModelId(detail.getBaseModelId())
				.build()
		);

		if (isExisted) {
			ProvidedProductEntity updatedEntity = providedProductRepository.getReferenceById(
				ProvidedProductId.builder()
					.businessCode(detail.getBusinessCode())
					.baseModelId(detail.getBaseModelId())
					.build()
			);
			updatedEntity = mapper.detailToEntity(detail, updatedEntity);
			updatedEntity = providedProductRepository.saveAndFlush(updatedEntity);
			return updatedEntity;
		}
		ProvidedProductEntity entity = mapper.detailToEntity(detail);
		entity.setBaseModel(baseModel);
		entity.setProvider(provider);
		entity = providedProductRepository.saveAndFlush(entity);
		return entity;
	}

	@Override
	public ProvidedProductDetail update(ProvidedProductDetail detail) {
		ProvidedProductEntity entity = providedProductRepository.getReferenceById(
			ProvidedProductId.builder()
				.businessCode(detail.getBusinessCode())
				.baseModelId(detail.getBaseModelId())
				.build()
		);
		entity = mapper.detailToEntity(detail, entity);
		entity = providedProductRepository.saveAndFlush(entity);
		return mapper.entityToDetail(entity);
	}

	@Override
	public PageResponse<ProvidedProductInfo> getInPage(Specification<ProvidedProductEntity> specification, Pageable pageable) {
		Page<ProvidedProductEntity> entities = providedProductRepository.findAll(specification, pageable);
		PageResponse<ProvidedProductInfo> providedProductPages
			= new PageResponse<>(entities.map(entity -> mapper.entityToInfo(entity)));
		return providedProductPages;
	}

	@Override
	public ProvidedProductDetail getDetail(long baseModelId, String providerId) {
		return null;
	}
}
