package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProvidedProductService;
import com.artiexh.api.service.ProviderService;
import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.data.jpa.entity.ProvidedProductEntity;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.data.jpa.repository.BaseModelRepository;
import com.artiexh.data.jpa.repository.ProviderRepository;
import com.artiexh.model.mapper.ProvidedProductMapper;
import com.artiexh.model.mapper.ProviderMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import com.artiexh.model.rest.providedproduct.ProvidedProductDetail;
import com.artiexh.model.rest.providedproduct.ProvidedProductInfo;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProviderServiceImpl implements ProviderService {
	private final ProviderMapper providerMapper;
	private final ProvidedProductMapper providedProductMapper;
	private final ProvidedProductService providedProductService;
	private final ProviderRepository providerRepository;
	private final BaseModelRepository baseModelRepository;
	@Override
	public ProviderDetail create(ProviderDetail detail) {
		ProviderEntity entity = providerMapper.detailToEntity(detail);
		entity = providerRepository.save(entity);
		detail = providerMapper.entityToDetail(entity);
		return detail;
	}

	@Override
	public ProviderDetail createProvidedProductList(String businessCode, Set<ProvidedProductDetail> details) {
		providedProductService.createList(businessCode, details);
		return getDetail(businessCode);
	}

	@Override
	public ProviderDetail createProvidedProduct(ProvidedProductDetail detail) {
		providedProductService.create(detail);
		return getDetail(detail.getBusinessCode());
	}

	@Override
	public ProviderDetail updateProvidedProduct(ProvidedProductDetail providedProductDetail) {
		providedProductService.update(providedProductDetail);
		return getDetail(providedProductDetail.getBusinessCode());
	}

	@Override
	public ProviderDetail update(ProviderDetail detail) {
		ProviderEntity entity = providerRepository.getReferenceById(detail.getBusinessCode());
		entity = providerMapper.detailToEntity(detail, entity);
		entity = providerRepository.save(entity);
		detail = providerMapper.entityToDetail(entity);
		return detail;
	}

	@Override
	public PageResponse<ProviderInfo> getInPage(Specification<ProviderEntity> specification, Pageable pageable) {
		//Todo: Update Filter
		Page<ProviderEntity> entities = providerRepository.findAll(specification, pageable);
		PageResponse<ProviderInfo> providerPages
			= new PageResponse<>(entities.map(entity -> providerMapper.entityToInfo(entity)));
		return providerPages;
	}

	@Override
	public ProviderDetail getDetail(String businessCode) {
		ProviderEntity entity = providerRepository.findById(businessCode)
			.orElseThrow(EntityNotFoundException::new);
		ProviderDetail detail = providerMapper.entityToDetail(entity);
		return detail;
	}
}
