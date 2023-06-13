package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.mapper.MerchAttachMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.product.ProductDetail;
import com.artiexh.model.rest.product.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.money.Monetary;
import javax.money.UnknownCurrencyException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.artiexh.api.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {
	private final ProductMapper productMapper;
	private final MerchAttachMapper merchAttachMapper;
	private final MerchAttachRepository attachRepository;
	private final ProductRepository productRepository;
	private final MerchCategoryRepository categoryRepository;
	private final MerchTagRepository tagRepository;
	private final ArtistRepository artistRepository;

	@Override
	public ProductDetail getDetail(long id) {
		MerchEntity merch = productRepository.findById(id).orElseThrow(
			() -> new ResponseStatusException(PRODUCT_NOT_FOUND.getCode(), PRODUCT_NOT_FOUND.getMessage())
		);
		return productMapper.entityToModelDetail(merch);
	}

	@Override
	public PageResponse<ProductInfo> getInPage(Specification<MerchEntity> specification, Pageable pageable) {
		Page<MerchEntity> products = productRepository.findAll(specification, pageable);
		Page<ProductInfo> productPage = products.map(entity -> productMapper.entityToModelInfo(entity));
		PageResponse<ProductInfo> productPageResponse = new PageResponse<>(productPage);

		return productPageResponse;
	}

	@Override
	public ProductDetail create(ProductDetail productModel) {
		Long userId;
		try {
			userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			throw new ResponseStatusException(ACCOUNT_INFO_NOT_FOUND.getCode(), ACCOUNT_INFO_NOT_FOUND.getMessage());
		}


		MerchEntity product = productMapper.domainModelToEntity(productModel);

		Set<MerchTagEntity> tagEntities = productModel.getTags().stream()
			.map(tagName -> MerchTagEntity.builder()
				.name(tagName)
				.build())
			.collect(Collectors.toSet());
		Set<MerchTagEntity> savedTagEntities = tagRepository.findAllByNameIn(new ArrayList<>(productModel.getTags()));
		tagEntities.removeAll(savedTagEntities);
		savedTagEntities.addAll(tagRepository.saveAll(tagEntities));

		MerchCategoryEntity categoryEntity = categoryRepository.findById(productModel.getCategoryId())
			.orElseThrow(() -> new ResponseStatusException(CATEGORY_NOT_FOUND.getCode(), CATEGORY_NOT_FOUND.getMessage()));

		Set<MerchAttachEntity> attachEntities = merchAttachMapper.domainModelsToEntities(productModel.getAttaches());
		List<MerchAttachEntity> attachEntitiesList = attachRepository.saveAll(attachEntities);

		ArtistEntity artist = artistRepository.findById(userId).orElseThrow(
			() -> new ResponseStatusException(ARTIST_NOT_FOUND.getCode(), ARTIST_NOT_FOUND.getMessage())
		);

		product.setOwner(artist);
		product.setTags(savedTagEntities);
		product.setCategory(categoryEntity);
		product.setAttaches(new HashSet<>(attachEntitiesList));
		product = productRepository.save(product);

		ProductDetail productResponse = productMapper.entityToModelDetail(product);
		productResponse.setId(product.getId());
		return productResponse;
	}

	@Override
	public ProductDetail update(ProductDetail productModel) {
		try {
			Monetary.getCurrency(productModel.getCurrency());
		} catch (UnknownCurrencyException e) {
			throw new ResponseStatusException(PRODUCT_CURRENCY_INVALID.getCode(), PRODUCT_CURRENCY_INVALID.getMessage());
		}

		MerchEntity product = productRepository.findById(productModel.getId()).orElseThrow(
			() -> new ResponseStatusException(PRODUCT_NOT_FOUND.getCode(), PRODUCT_NOT_FOUND.getMessage())
		);

		product = productMapper.domainModelToEntity(productModel, product);

		Set<MerchTagEntity> tagEntities = productModel.getTags().stream()
			.map(tagName -> MerchTagEntity.builder()
				.name(tagName)
				.build())
			.collect(Collectors.toSet());
		Set<MerchTagEntity> savedTagEntities = tagRepository.findAllByNameIn(new ArrayList<>(productModel.getTags()));
		tagEntities.removeAll(savedTagEntities);
		savedTagEntities.addAll(tagRepository.saveAll(tagEntities));

		MerchCategoryEntity categoryEntity = categoryRepository.findById(productModel.getCategoryId())
			.orElseThrow(() -> new ResponseStatusException(CATEGORY_NOT_FOUND.getCode(), CATEGORY_NOT_FOUND.getMessage()));


		Set<MerchAttachEntity> attachEntities = merchAttachMapper.domainModelsToEntities(productModel.getAttaches());
		List<MerchAttachEntity> attachEntitiesList = attachRepository.saveAll(attachEntities);

		product.setTags(savedTagEntities);
		product.setCategory(categoryEntity);
		product.setAttaches(new HashSet<>(attachEntitiesList));
		product = productRepository.save(product);

		ProductDetail productResponse = productMapper.entityToModelDetail(product);
		productResponse.setId(product.getId());
		return productResponse;
	}

	@Override
	public void delete(long id) {
		productRepository.delete(id, (byte) MerchStatus.NOT_AVAILABLE.getValue());
	}
}