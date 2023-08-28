package com.artiexh.api.service;

import com.artiexh.model.domain.ProvidedProductBase;

public interface ProvidedProductBaseService {
	ProvidedProductBase create(ProvidedProductBase product);
	ProvidedProductBase update(ProvidedProductBase product);
	void delete(String businessCode, Long productBaseId);
}
