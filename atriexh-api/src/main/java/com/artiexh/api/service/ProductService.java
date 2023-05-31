package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.domain.Merch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {
	Merch getDetail(long id);
	PageResponse<Merch> getInPage(Specification<MerchEntity> specification, Pageable pageable);
	List<Merch> getInList(Specification<MerchEntity> specification);
	Merch create(Merch merch);
	Merch update (Merch merch);
	void delete(long id);
}
