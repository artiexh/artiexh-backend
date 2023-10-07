package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProductBaseService;
import com.artiexh.data.jpa.entity.OptionValueEntity;
import com.artiexh.data.jpa.entity.ProductBaseEntity;
import com.artiexh.data.jpa.entity.ProductOptionEntity;
import com.artiexh.data.jpa.repository.MediaRepository;
import com.artiexh.data.jpa.repository.OptionValueRepository;
import com.artiexh.data.jpa.repository.ProductBaseRepository;
import com.artiexh.data.jpa.repository.ProductOptionRepository;
import com.artiexh.model.domain.OptionValue;
import com.artiexh.model.domain.ProductBase;
import com.artiexh.model.domain.ProductOption;
import com.artiexh.model.mapper.CycleAvoidingMappingContext;
import com.artiexh.model.mapper.ProductBaseMapper;
import com.artiexh.model.mapper.ProductOptionMapper;
import com.artiexh.model.rest.productbase.ProductBaseFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductBaseServiceImpl implements ProductBaseService {
	private final ProductBaseMapper productBaseMapper;
	private final ProductBaseRepository productBaseRepository;
	private final ProductOptionRepository productOptionRepository;
	private final OptionValueRepository optionValueRepository;
	private final MediaRepository mediaRepository;
	private final ProductOptionMapper optionMapper;

	@Override
	@Transactional
	public ProductBase create(ProductBase product) {
		ProductBaseEntity entity = productBaseMapper.domainToEntity(product);

		entity.setHasVariant(false);
		entity.setModelFile(mediaRepository.getReferenceById(product.getModelFile().getId()));

		entity = productBaseRepository.save(entity);

		for (ProductOptionEntity productOption : entity.getProductOptions()) {
			productOption.setProductId(entity.getId());
			productOption = productOptionRepository.save(productOption);

			for (OptionValueEntity optionValue : productOption.getOptionValues()) {
				optionValue.setOptionId(productOption.getId());
				optionValueRepository.save(optionValue);
			}
		}

		product = productBaseMapper.entityToDomain(entity, new CycleAvoidingMappingContext());
		return product;
	}

	@Override
	@Transactional
	public ProductBase update(ProductBase product) {
		ProductBaseEntity entity = productBaseRepository.findById(product.getId())
			.orElseThrow(EntityNotFoundException::new);

		entity = productBaseMapper.domainToEntity(product, entity);

		entity = productBaseRepository.save(entity);

		for (ProductOption productOption : product.getProductOptions()) {
			ProductOptionEntity option = optionMapper.domainToEntity(productOption);
			option.setProductId(entity.getId());
			productOptionRepository.save(option);

			for (OptionValue optionValue : productOption.getOptionValues()) {
				OptionValueEntity optionValueEntity = optionMapper.domainToEntity(optionValue);
				optionValueEntity.setOptionId(productOption.getId());
				optionValueRepository.save(optionValueEntity);
			}
		}
		return product;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductBase> getInPage(Specification<ProductBaseEntity> specification, Pageable pageable) {
		Page<ProductBaseEntity> entities = productBaseRepository.findAll(specification, pageable);
		return entities.map(entity -> productBaseMapper.entityToDomain(entity, new CycleAvoidingMappingContext()));
	}

	@Override
	@Transactional(readOnly = true)
	public ProductBase getById(Long id) {
		ProductBaseEntity entity = productBaseRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + id));
		return productBaseMapper.entityToDomain(entity, new CycleAvoidingMappingContext());
	}
}
