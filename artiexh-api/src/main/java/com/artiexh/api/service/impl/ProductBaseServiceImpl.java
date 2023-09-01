package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductBaseService;
import com.artiexh.data.jpa.entity.ProductBaseEntity;
import com.artiexh.data.jpa.repository.ProductBaseRepository;
import com.artiexh.data.jpa.repository.ProvidedProductBaseRepository;
import com.artiexh.model.domain.ProductBase;
import com.artiexh.model.mapper.ProductBaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductBaseServiceImpl implements ProductBaseService {
	private final ProductBaseMapper productBaseMapper;
	private final ProductBaseRepository productBaseRepository;
	@Override
	public ProductBase create(ProductBase product) {
		ProductBaseEntity entity = productBaseMapper.domainToEntity(product);
		productBaseRepository.save(entity);

		product.setId(entity.getId());
		return product;
	}

	@Override
	public Page<ProductBase> getInPage(Pageable pageable) {
		Page<ProductBaseEntity> entities = productBaseRepository.findAll(pageable);
		return entities.map(productBaseMapper::entityToDomain);
	}
}
