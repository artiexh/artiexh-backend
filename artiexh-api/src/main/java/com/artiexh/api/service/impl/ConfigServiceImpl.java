package com.artiexh.api.service.impl;

import com.artiexh.api.service.ConfigService;
import com.artiexh.api.service.product.OpenSearchProductService;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
	private final ProductRepository productRepository;
	private final OpenSearchProductService openSearchProductService;
	private final ProductMapper productMapper;

	@Override
	public void syncProductToOpenSearch() {
		productRepository.streamAllByAvailableStatus()
			.map(productMapper::entityToDocument)
			.forEach(openSearchProductService::save);
	}

}
