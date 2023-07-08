package com.artiexh.api.service;

import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelFilter;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import org.springframework.data.domain.Pageable;

public interface BaseModelService {
	BaseModelDetail create(BaseModelDetail detail);
	BaseModelDetail update(BaseModelDetail detail);
	PageResponse<BaseModelInfo> getInPage(BaseModelFilter filter, Pageable pageable);
	BaseModelDetail getDetail(long id);
}
