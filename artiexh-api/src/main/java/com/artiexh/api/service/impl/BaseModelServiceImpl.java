package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.BaseModelService;
import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.data.jpa.repository.BaseModelRepository;
import com.artiexh.model.mapper.BaseModelMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelFilter;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
		BaseModelEntity entity = baseModelRepository.findById(detail.getId())
			.orElseThrow(() -> new ResponseStatusException(
				ErrorCode.BASE_MODEL_NOT_FOUND.getCode(),
				ErrorCode.BASE_MODEL_NOT_FOUND.getMessage()));
		entity = baseModelMapper.detailToEntity(detail, entity);
		entity = baseModelRepository.save(entity);
		detail = baseModelMapper.entityToDetail(entity);
		return detail;
	}

	@Override
	public PageResponse<BaseModelInfo> getInPage(BaseModelFilter filter, Pageable pageable) {
		Page<BaseModelEntity> baseModelEntities = baseModelRepository.findAll(pageable);
		PageResponse<BaseModelInfo> baseModelPages
			= new PageResponse<>(baseModelEntities.map(entity -> baseModelMapper.entityToInfo(entity)));
		return baseModelPages;
	}

	@Override
	public BaseModelDetail getDetail(long id) {
		BaseModelEntity entity = baseModelRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(
				ErrorCode.BASE_MODEL_NOT_FOUND.getCode(),
				ErrorCode.BASE_MODEL_NOT_FOUND.getMessage()));
		BaseModelDetail detail = baseModelMapper.entityToDetail(entity);
		return detail;
	}
}
