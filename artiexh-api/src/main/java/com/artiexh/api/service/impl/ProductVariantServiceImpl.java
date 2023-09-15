package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProductVariantService;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.repository.ProductBaseRepository;
import com.artiexh.data.jpa.repository.ProductVariantRepository;
import com.artiexh.data.jpa.repository.ProviderRepository;
import com.artiexh.data.jpa.repository.VariantCombinationRepository;
import com.artiexh.model.domain.ProductVariant;
import com.artiexh.model.mapper.ProductVariantMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
	private final ProductVariantRepository repository;
	private final ProductVariantMapper mapper;
	private final ProviderRepository providerRepository;
	private final ProductBaseRepository productBaseRepository;
	private final VariantCombinationRepository variantCombinationRepository;

	@Override
	@Transactional
	public ProductVariant create(ProductVariant product) {
		repository.findByProductBaseIdAndBusinessCode(
			product.getProductBaseId(),
			product.getBusinessCode()
		).ifPresent(entity -> {
			throw new IllegalArgumentException(ErrorCode.PRODUCT_EXISTED.getMessage() + entity.getId());
		});

		ProductVariantEntity entity = mapper.domainToEntity(product);

		repository.save(entity);

		entity.getVariantCombinations().forEach(combination -> {
			combination.getId().setVariantId(entity.getId());
			variantCombinationRepository.save(combination);
		});
		return product;
	}

	@Override
	public ProductVariant update(ProductVariant product) {
		repository.findByProductBaseIdAndBusinessCode(product.getProductBaseId(), product.getBusinessCode())
			.orElseThrow(EntityNotFoundException::new);
		ProductVariantEntity entity = mapper.domainToEntity(product);
		repository.save(entity);
		return product;
	}

	@Override
	public void delete(String businessCode, Long productBaseId) {
		ProductVariantEntity product = repository.findByProductBaseIdAndBusinessCode(productBaseId, businessCode)
			.orElseThrow(EntityNotFoundException::new);
		repository.deleteById(product.getId());
	}

	@Override
	public ProductVariant getById(Long id) {
		ProductVariantEntity entity = repository.findById(id)
			.orElseThrow(EntityNotFoundException::new);
		return mapper.entityToDomain(entity);
	}

	@Override
	public Page<ProductVariant> getAll(Specification<ProductVariantEntity> specification, Pageable pageable) {
		Page<ProductVariantEntity> productPage = repository.findAll(specification, pageable);
		return productPage.map(mapper::entityToDomain);
	}
}
