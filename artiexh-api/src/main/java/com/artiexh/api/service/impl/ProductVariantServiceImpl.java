package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProductVariantService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.entity.embededmodel.ProductVariantProviderId;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.ProductVariant;
import com.artiexh.model.domain.ProductVariantProvider;
import com.artiexh.model.domain.VariantCombination;
import com.artiexh.model.mapper.CycleAvoidingMappingContext;
import com.artiexh.model.mapper.ProductVariantMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
			.toList());

		if (allowedProviders != product.getProviderConfigs().size()) {
			throw new IllegalArgumentException(ErrorCode.PROVIDER_INVALID.getMessage());
		}

		//Validation option id and option value
		List<ProductOptionEntity> requiredOption = productOptionRepository.findProductOptionEntityByProductIdAndIsOptional(product.getProductBaseId(),false);
		int numOfRequiredOption = requiredOption.size();
		int numOfRequiredOptionInput = 0;
		for (VariantCombination combination : product.getVariantCombinations()){
			if (!combination.getIsOptional()) {
				numOfRequiredOptionInput ++;
				boolean isValidRequiredOption = requiredOption.stream().anyMatch(option -> option.getId().equals(combination.getOptionId()));
				if (!isValidRequiredOption) {
                    throw new IllegalArgumentException(ErrorCode.REQUIRED_OPTION_NOT_FOUND.getMessage() + combination.getOptionId());
                }
			}
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
		};

		if (numOfRequiredOptionInput != numOfRequiredOption) {
			throw new IllegalArgumentException(ErrorCode.REQUIRED_OPTION_NOT_FOUND.getMessage());
		}

		ProductVariantEntity entity = mapper.domainToEntity(product);
		repository.save(entity);

		for (VariantCombination combination : product.getVariantCombinations()) {
			ProductVariantCombinationEntity combinationEntity = mapper.domainToEntity(combination);
			combinationEntity.setId(ProductVariantCombinationEntityId.builder()
				.optionValueId(combination.getOptionValueId())
				.variantId(entity.getId())
				.build());
			combinationEntity.setProductVariant(entity);
			variantCombinationRepository.save(combinationEntity);
		};

		for (ProductVariantProvider providerConfig : product.getProviderConfigs())  {
			ProductVariantProviderEntity provider = mapper.domainToEntity(providerConfig);
			provider.setId(ProductVariantProviderId.builder()
				.businessCode(providerConfig.getBusinessCode())
				.productVariantId(entity.getId())
				.build());
			provider.setProductVariant(entity);
			provider.setProvider(providerRepository.findById(providerConfig.getBusinessCode())
				.orElseThrow(EntityNotFoundException::new));
			productVariantProviderRepository.save(provider);
		};

		product.setId(entity.getId());
		return product;
	}

	@Override
	@Transactional
	public Set<ProductVariant> create(Set<ProductVariant> products) {
		Set<ProductVariant> result = new HashSet<>();
		for (ProductVariant productVariant : products) {
			result.add(create(productVariant));
		}
		return result;
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

		Set<ProductVariantCombinationEntity> combinations = new HashSet<>();
		entity.getVariantCombinations().clear();
		for (VariantCombination combination : product.getVariantCombinations()) {
			ProductVariantCombinationEntity combinationEntity = mapper.domainToEntity(combination);
			combinationEntity.setId(ProductVariantCombinationEntityId.builder()
				.optionValueId(combination.getOptionValueId())
				.variantId(entity.getId())
				.build());
			combinationEntity.setProductVariant(entity);
			variantCombinationRepository.save(combinationEntity);
			combinations.add(combinationEntity);
		}
		entity.getVariantCombinations().addAll(combinations);


		Set<ProductVariantProviderEntity> providers = new HashSet<>();
		entity.getProviderConfigs().clear();
		for (ProductVariantProvider providerConfig : product.getProviderConfigs()) {
			ProductVariantProviderEntity provider = mapper.domainToEntity(providerConfig);
			provider.setId(ProductVariantProviderId.builder()
				.businessCode(providerConfig.getBusinessCode())
				.productVariantId(entity.getId())
				.build());
			provider.setProductVariant(entity);
			provider.setProvider(providerRepository.findById(providerConfig.getBusinessCode())
				.orElseThrow(EntityNotFoundException::new));
			productVariantProviderRepository.save(provider);
			providers.add(provider);
		};
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
