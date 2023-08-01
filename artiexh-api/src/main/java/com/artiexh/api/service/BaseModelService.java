package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BaseModelService {
	BaseModelDetail create(BaseModelDetail detail);

	BaseModelDetail update(BaseModelDetail detail);

	PageResponse<BaseModelInfo> getInPage(Specification<BaseModelEntity> specification, Pageable pageable);

	BaseModelDetail getDetail(long id);
}
