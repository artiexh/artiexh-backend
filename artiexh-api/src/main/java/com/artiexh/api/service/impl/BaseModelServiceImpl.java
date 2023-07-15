package com.artiexh.api.service.impl;

import com.artiexh.api.service.BaseModelService;
import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.data.jpa.repository.BaseModelRepository;
import com.artiexh.model.mapper.BaseModelMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BaseModelServiceImpl implements BaseModelService {
	private BaseModelMapper baseModelMapper;
	private BaseModelRepository baseModelRepository;

	@Override
	public BaseModelDetail create(BaseModelDetail detail) {
		BaseModelEntity entity = baseModelMapper.detailToEntity(detail);
		entity = baseModelRepository.save(entity);
		detail = baseModelMapper.entityToDetail(entity);
		return detail;
	}

	@Override
	public BaseModelDetail update(BaseModelDetail detail) {
		BaseModelEntity entity = baseModelRepository.getReferenceById(detail.getId());
		entity = baseModelMapper.detailToEntity(detail, entity);
		entity = baseModelRepository.save(entity);
		detail = baseModelMapper.entityToDetail(entity);
		return detail;
	}

	@Override
	public PageResponse<BaseModelInfo> getInPage(Specification<BaseModelEntity> specification, Pageable pageable) {
		//Todo: Update Filter
		Page<BaseModelEntity> baseModelEntities = baseModelRepository.findAll(specification, pageable);
		PageResponse<BaseModelInfo> baseModelPages
			= new PageResponse<>(baseModelEntities.map(entity -> baseModelMapper.entityToInfo(entity)));
		return baseModelPages;
	}

	@Override
	public BaseModelDetail getDetail(long id) {
		BaseModelEntity entity = baseModelRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);
		BaseModelDetail detail = baseModelMapper.entityToDetail(entity);
		return detail;
	}
}
