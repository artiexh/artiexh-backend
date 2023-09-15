package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.rest.provider.ProviderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProviderService {
	Provider create(Provider provider);
	Provider getById(String businessCode);
	Page<Provider> getInPage(Pageable pageable);
	Page<Provider> getInPage(Specification<ProviderEntity> specification, Pageable pageable);
}
