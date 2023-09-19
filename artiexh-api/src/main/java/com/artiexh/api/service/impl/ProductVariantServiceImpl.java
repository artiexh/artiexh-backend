package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProductVariantService;
import com.artiexh.data.jpa.entity.ProductOptionEntity;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.ProductVariant;
import com.artiexh.model.domain.ProductVariantProvider;
import com.artiexh.model.mapper.CycleAvoidingMappingContext;
import com.artiexh.model.mapper.ProductVariantMapper;
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
public class ProductVariantServiceImpl implements ProductVariantService {
	private final ProductVariantRepository repository;
	private final ProductVariantMapper mapper;
	private final ProductOptionRepository productOptionRepository;
	private final VariantCombinationRepository variantCombinationRepository;
	private final ProductVariantProviderRepository productVariantProviderRepository;
	private final ProviderRepository providerRepository;

	@Override
	@Transactional
	public ProductVariant create(ProductVariant product) {

		//Validate provider
		int allowedProviders = providerRepository.countProvider(product.getProductBaseId(), product.getProviderConfigs().stream()
			.map(ProductVariantProvider::getBusinessCode)
			.collect(Collectors.toList()));

		if (allowedProviders != product.getProviderConfigs().size()) {
			throw new IllegalArgumentException(ErrorCode.PROVIDER_INVALID.getMessage());
		}

		//Validation option id and option value
		product.getVariantCombinations().forEach(combination -> {
			ProductOptionEntity productOption = productOptionRepository.findProductOptionEntityByProductIdAndId(
				product.getProductBaseId(),
					combination.getOptionId())
				.orElseThrow(()
					-> new EntityNotFoundException(ErrorCode.OPTION_NOT_FOUND.getMessage() + combination.getOptionId())
				);
			boolean isValidOption = productOption.getOptionValues().stream()
				.anyMatch(option -> option.getId().equals(combination.getOptionValueId()));
			if (!isValidOption) {
				throw new IllegalArgumentException(ErrorCode.OPTION_VALUE_INVALID.getMessage() + combination.getOptionId());
			}
		});

		ProductVariantEntity entity = mapper.domainToEntity(product);

		repository.save(entity);

		entity.getVariantCombinations().forEach(combination -> {
			combination.getId().setVariantId(entity.getId());
			variantCombinationRepository.save(combination);
		});

		entity.getProviderConfigs().forEach(providerConfig -> {
			providerConfig.getId().setProductVariantId(entity.getId());
			providerConfig.setProvider(ProviderEntity.builder()
				.businessCode(providerConfig.getId().getBusinessCode())
				.build());
			productVariantProviderRepository.save(providerConfig);
		});
		return mapper.entityToDomain(entity, new CycleAvoidingMappingContext());
	}

	@Override
	@Transactional
	public ProductVariant update(ProductVariant product) {
		ProductVariantEntity entity = repository.findById(product.getId())
			.orElseThrow(() ->
				new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + product.getId())
			);

		//Validate provider
		int allowedProviders = providerRepository.countProvider(
			entity.getProductBaseId(),
			product.getProviderConfigs().stream()
				.map(ProductVariantProvider::getBusinessCode)
				.toList()
		);

		if (allowedProviders != product.getProviderConfigs().size()) {
			throw new IllegalArgumentException(ErrorCode.PROVIDER_INVALID.getMessage());
		}

		//Validation option id and option value
		product.getVariantCombinations().forEach(combination -> {
			ProductOptionEntity productOption = productOptionRepository.findProductOptionEntityByProductIdAndId(
					product.getProductBaseId(),
					combination.getOptionId())
				.orElseThrow(()
					-> new EntityNotFoundException(ErrorCode.OPTION_NOT_FOUND.getMessage() + combination.getOptionId())
				);
			boolean isValidOption = productOption.getOptionValues().stream()
				.anyMatch(option -> option.getId().equals(combination.getOptionValueId()));
			if (!isValidOption) {
				throw new IllegalArgumentException(ErrorCode.OPTION_VALUE_INVALID.getMessage() + combination.getOptionId());
			}
		});

		entity = mapper.domainToEntity(product, entity);

		entity.getVariantCombinations().forEach(combination -> {

			combination.getId().setVariantId(product.getId());
			variantCombinationRepository.save(combination);
		});
		entity.getProviderConfigs().forEach(providerConfig -> {
			providerConfig.getId().setProductVariantId(product.getId());
			providerConfig.setProvider(ProviderEntity.builder()
				.businessCode(providerConfig.getId().getBusinessCode())
				.build());
			productVariantProviderRepository.save(providerConfig);
		});
		repository.save(entity);
		return product;
	}

	@Override
	public void delete(String businessCode, Long productBaseId) {
		ProductVariantEntity product = repository.findById(productBaseId)
			.orElseThrow(EntityNotFoundException::new);
		repository.deleteById(product.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public ProductVariant getById(Long id) {
		ProductVariantEntity entity = repository.findById(id)
			.orElseThrow(EntityNotFoundException::new);
		return mapper.entityToDomain(entity, new CycleAvoidingMappingContext());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductVariant> getAll(Specification<ProductVariantEntity> specification, Pageable pageable) {
		Page<ProductVariantEntity> productPage = repository.findAll(specification, pageable);
		return productPage.map(product -> mapper.entityToDomain(product, new CycleAvoidingMappingContext()));
	}
}
