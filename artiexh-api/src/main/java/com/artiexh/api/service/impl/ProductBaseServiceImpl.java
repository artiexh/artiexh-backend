package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProductBaseService;
import com.artiexh.api.service.ProductVariantService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.MediaRepository;
import com.artiexh.data.jpa.repository.OptionValueRepository;
import com.artiexh.data.jpa.repository.ProductBaseRepository;
import com.artiexh.data.jpa.repository.ProductOptionRepository;
import com.artiexh.model.domain.ProductBase;
import com.artiexh.model.domain.ProductVariant;
import com.artiexh.model.mapper.CycleAvoidingMappingContext;
import com.artiexh.model.mapper.ProductBaseMapper;
import com.artiexh.model.mapper.ProviderMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBaseServiceImpl implements ProductBaseService {
	private final ProductBaseMapper productBaseMapper;
	private final ProductBaseRepository productBaseRepository;
	private final ProductOptionRepository productOptionRepository;
	private final OptionValueRepository optionValueRepository;
	private final MediaRepository mediaRepository;
	private final ProviderMapper providerMapper;
	private final ProductVariantService variantService;

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
				optionValue.setOption(ProductOptionEntity.builder().id(productOption.getId()).build());
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

		entity.setModelFile(MediaEntity.builder().id(product.getModelFile().getId()).build());
		entity.setCategory(ProductCategoryEntity.builder().id(product.getCategory().getId()).build());

		for (ProductOptionEntity option : entity.getProductOptions()) {
			option.setProductId(entity.getId());
			productOptionRepository.save(option);

			for (OptionValueEntity optionValue : option.getOptionValues()) {
				optionValue.setOption(ProductOptionEntity.builder().id(option.getId()).build());
				optionValueRepository.save(optionValue);
			}
		}

		productBaseRepository.save(entity);

		product = productBaseMapper.entityToDomain(entity, new CycleAvoidingMappingContext());
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

	@Override
	@Transactional
	public ProductBase updateProductBaseConfig(ProductBase product) {
		ProductBaseEntity entity = productBaseRepository.findById(product.getId())
			.orElseThrow(EntityNotFoundException::new);

		for (ProductVariantEntity variant : entity.getProvidedModels()) {
			boolean isMatchedAllVariant = product.getProductVariants().stream().anyMatch(existedVariant -> existedVariant.getId().equals(variant.getId()));
			if (!isMatchedAllVariant) {
				throw new IllegalArgumentException(ErrorCode.VARIANT_NOT_FOUND.getMessage() + variant.getId());
			}
		}

		entity.setProviders(
			product.getProviders().stream()
				.map(providerMapper::domainToEntity)
				.collect(Collectors.toSet())
		);

		productBaseRepository.save(entity);

		for (ProductVariant variant : product.getProductVariants()) {
			variantService.updateProviderConfig(variant.getId(), variant.getProviderConfigs());
		}

		return productBaseMapper.entityToDomain(entity, new CycleAvoidingMappingContext());
	}
}
