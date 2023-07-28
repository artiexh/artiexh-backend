package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProvidedModelService;
import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.data.jpa.entity.ProvidedModelEntity;
import com.artiexh.data.jpa.entity.ProvidedModelId;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.data.jpa.repository.BaseModelRepository;
import com.artiexh.data.jpa.repository.ProvidedProductRepository;
import com.artiexh.data.jpa.repository.ProviderRepository;
import com.artiexh.model.mapper.ProvidedModelMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.providedproduct.ProvidedModelDetail;
import com.artiexh.model.rest.providedproduct.ProvidedModelInfo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProvidedModelServiceImpl implements ProvidedModelService {
	private final ProvidedProductRepository providedProductRepository;
	private final ProviderRepository providerRepository;
	private final BaseModelRepository baseModelRepository;
	private final ProvidedModelMapper mapper;
	@Override
	public ProvidedModelDetail create(ProvidedModelDetail detail) {
		ProvidedModelEntity entity = mapper.detailToEntity(detail);
		ProviderEntity provider = providerRepository.getReferenceById(detail.getBusinessCode());
		BaseModelEntity baseModel = baseModelRepository.getReferenceById(detail.getBaseModelId());
		entity.setBaseModel(baseModel);
		entity.setProvider(provider);
		providedProductRepository.saveAndFlush(entity);
		return detail;
	}

	@Override
	public void createList(String businessCode, Set<ProvidedModelDetail> details) {

		for (ProvidedModelDetail detail : details) {
			detail.setBusinessCode(businessCode);
			createOrUpdate(detail);
		}
	}

	private void createOrUpdate(ProvidedModelDetail detail) {
		ProvidedModelEntity providedModel = null;
		try {
			providedModel = providedProductRepository.getReferenceById(new ProvidedModelId(detail.getBusinessCode(), detail.getBaseModelId()));
		} catch (EntityNotFoundException exception) {
			log.info("Create new provided model");
			BaseModelEntity baseModel = baseModelRepository.getReferenceById(detail.getBaseModelId());
			ProviderEntity provider = providerRepository.getReferenceById(detail.getBusinessCode());

			providedModel = mapper.detailToEntity(detail);
			providedModel.setBaseModel(baseModel);
			providedModel.setProvider(provider);
			providedProductRepository.save(providedModel);
			return ;
		}
		log.info("Update new provided model");
		providedModel = mapper.detailToEntity(detail, providedModel);
		providedProductRepository.save(providedModel);
		return ;
	}

	@Override
	public ProvidedModelDetail update(ProvidedModelDetail detail) {
		ProvidedModelEntity entity = providedProductRepository.getReferenceById(
			ProvidedModelId.builder()
				.businessCode(detail.getBusinessCode())
				.baseModelId(detail.getBaseModelId())
				.build()
		);
		entity = mapper.detailToEntity(detail, entity);
		entity = providedProductRepository.saveAndFlush(entity);
		return mapper.entityToDetail(entity);
	}

	@Override
	public PageResponse<ProvidedModelInfo> getInPage(Specification<ProvidedModelEntity> specification, Pageable pageable) {
		Page<ProvidedModelEntity> entities = providedProductRepository.findAll(specification, pageable);
		PageResponse<ProvidedModelInfo> providedProductPages
			= new PageResponse<>(entities.map(entity -> mapper.entityToInfo(entity)));
		return providedProductPages;
	}

	@Override
	public ProvidedModelDetail getDetail(long baseModelId, String providerId) {
		return null;
	}
}
