package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProductVariantService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.entity.embededmodel.ProductVariantProviderId;
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

import java.util.HashSet;
import java.util.Set;
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
					-> new IllegalArgumentException(ErrorCode.OPTION_NOT_FOUND.getMessage() + combination.getOptionId())
				);
			boolean isValidOption = productOption.getOptionValues().stream()
				.anyMatch(option -> option.getId().equals(combination.getOptionValueId()));
			if (!isValidOption) {
				throw new IllegalArgumentException(ErrorCode.OPTION_VALUE_INVALID.getMessage() + combination.getOptionId());
			}
		});

		ProductVariantEntity entity = mapper.domainToEntity(product);
		repository.save(entity);

		ProductVariantEntity savedEntity = entity;
		product.getVariantCombinations().forEach(combination -> {
			ProductVariantCombinationEntity combinationEntity = mapper.domainToEntity(combination);
			combinationEntity.setId(ProductVariantCombinationEntityId.builder()
				.optionValueId(combination.getOptionValueId())
				.variantId(savedEntity.getId())
				.build());
			combinationEntity.setProductVariant(savedEntity);
			variantCombinationRepository.save(combinationEntity);
		});

		product.getProviderConfigs().forEach(providerConfig -> {
			ProductVariantProviderEntity provider = mapper.domainToEntity(providerConfig);
			provider.setId(ProductVariantProviderId.builder()
				.businessCode(providerConfig.getBusinessCode())
				.productVariantId(savedEntity.getId())
				.build());
			provider.setProductVariant(savedEntity);
			provider.setProvider(providerRepository.findById(providerConfig.getBusinessCode())
				.orElseThrow(EntityNotFoundException::new));
			productVariantProviderRepository.save(provider);
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

		Long productBaseId = entity.getProductBaseId();

		//Validate provider
		int allowedProviders = providerRepository.countProvider(
			productBaseId,
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
					productBaseId,
					combination.getOptionId())
				.orElseThrow(()
					-> new IllegalArgumentException(ErrorCode.OPTION_NOT_FOUND.getMessage() + combination.getOptionId())
				);
			boolean isValidOption = productOption.getOptionValues().stream()
				.anyMatch(option -> option.getId().equals(combination.getOptionValueId()));
			if (!isValidOption) {
				throw new IllegalArgumentException(ErrorCode.OPTION_VALUE_INVALID.getMessage() + combination.getOptionId());
			}
		});

		entity = mapper.domainToEntity(product, entity);

		ProductVariantEntity savedEntity = entity;

		Set<ProductVariantCombinationEntity> combiantions = new HashSet<>();
		entity.getVariantCombinations().clear();
		product.getVariantCombinations().forEach(combination -> {
			ProductVariantCombinationEntity combinationEntity = mapper.domainToEntity(combination);
			combinationEntity.setId(ProductVariantCombinationEntityId.builder()
				.optionValueId(combination.getOptionValueId())
				.variantId(savedEntity.getId())
				.build());
			combinationEntity.setProductVariant(savedEntity);
			variantCombinationRepository.save(combinationEntity);
			combiantions.add(combinationEntity);
		});
		entity.getVariantCombinations().addAll(combiantions);


		Set<ProductVariantProviderEntity> providers = new HashSet<>();
		entity.getProviderConfigs().clear();
		product.getProviderConfigs().forEach(providerConfig -> {
			ProductVariantProviderEntity provider = mapper.domainToEntity(providerConfig);
			provider.setId(ProductVariantProviderId.builder()
				.businessCode(providerConfig.getBusinessCode())
				.productVariantId(savedEntity.getId())
				.build());
			provider.setProductVariant(savedEntity);
			provider.setProvider(providerRepository.findById(providerConfig.getBusinessCode())
				.orElseThrow(EntityNotFoundException::new));
			productVariantProviderRepository.save(provider);
			providers.add(provider);
		});
		entity.getProviderConfigs().addAll(providers);

		repository.save(entity);
		return mapper.entityToDomain(entity, new CycleAvoidingMappingContext());
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
