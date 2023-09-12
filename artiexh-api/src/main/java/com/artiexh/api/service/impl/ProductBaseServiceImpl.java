package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductBaseService;
import com.artiexh.data.jpa.entity.OptionValueEntity;
import com.artiexh.data.jpa.entity.ProductBaseEntity;
import com.artiexh.data.jpa.entity.ProductOptionEntity;
import com.artiexh.data.jpa.repository.OptionValueRepository;
import com.artiexh.data.jpa.repository.ProductBaseRepository;
import com.artiexh.data.jpa.repository.ProductOptionRepository;
import com.artiexh.data.jpa.repository.ProvidedProductBaseRepository;
import com.artiexh.model.domain.OptionValue;
import com.artiexh.model.domain.ProductBase;
import com.artiexh.model.domain.ProductOption;
import com.artiexh.model.mapper.ProductBaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductBaseServiceImpl implements ProductBaseService {
	private final ProductBaseMapper productBaseMapper;
	private final ProductBaseRepository productBaseRepository;
	private final ProductOptionRepository productOptionRepository;
	private final OptionValueRepository optionValueRepository;
	@Override
	@Transactional
	public ProductBase create(ProductBase product) {
		ProductBaseEntity entity = productBaseMapper.domainToEntity(product);
		entity = productBaseRepository.save(entity);

		for(ProductOptionEntity productOption : entity.getProductOptions()) {
			productOption.setProductId(entity.getId());
			productOption = productOptionRepository.save(productOption);

			for(OptionValueEntity optionValue: productOption.getOptionValues()) {
				optionValue.setOptionId(productOption.getId());
				optionValueRepository.save(optionValue);
			}
		}

		product.setId(entity.getId());
		return product;
	}

	@Override
	public Page<ProductBase> getInPage(Pageable pageable) {
		Page<ProductBaseEntity> entities = productBaseRepository.findAll(pageable);
		return entities.map(productBaseMapper::entityToDomain);
	}
}
