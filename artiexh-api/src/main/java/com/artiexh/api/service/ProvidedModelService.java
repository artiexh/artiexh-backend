package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.ProvidedModelEntity;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.providedproduct.ProvidedModelDetail;
import com.artiexh.model.rest.providedproduct.ProvidedModelInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public interface ProvidedModelService {
	ProvidedModelDetail create(ProvidedModelDetail detail);

	void createList(String businessCode, Set<ProvidedModelDetail> detail);

	ProvidedModelDetail update(ProvidedModelDetail detail);

	PageResponse<ProvidedModelInfo> getInPage(Specification<ProvidedModelEntity> specification, Pageable pageable);

	ProvidedModelDetail getDetail(long baseModelId, String providerId);
}
