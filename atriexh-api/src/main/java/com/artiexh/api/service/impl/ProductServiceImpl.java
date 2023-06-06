package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.domain.Merch;
import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.mapper.MerchAttachMapper;
import com.artiexh.model.product.ProductDetail;
import com.artiexh.model.product.ProductInfo;
import com.artiexh.model.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
	public List<ProductInfo> getInList(Specification<MerchEntity> specification) {
		List<MerchEntity> products = productRepository.findAll(specification);
		List<ProductInfo> productListResponse = productMapper.entitiesToDomainModels(products);
		return productListResponse;
	}

	@Override
	public ProductDetail create(ProductDetail productModel) {
		try {
			Monetary.getCurrency(productModel.getCurrency());
		} catch (UnknownCurrencyException e) {
			throw new ResponseStatusException(PRODUCT_CURRENCY_INVALID.getCode(), PRODUCT_CURRENCY_INVALID.getMessage());
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

		Set<MerchCategoryEntity> categoryEntities = categoryRepository.findAllByNameIn(new ArrayList<>(productModel.getCategories()));

		Set<MerchAttachEntity> attachEntities = merchAttachMapper.domainModelsToEntities(productModel.getAttaches());
		List<MerchAttachEntity> attachEntitiesList = attachRepository.saveAll(attachEntities);

		ArtistEntity artist = artistRepository.findById(productModel.getOwnerId()).orElseThrow(
			() -> new ResponseStatusException(ARTIST_NOT_FOUND.getCode(), ARTIST_NOT_FOUND.getMessage())
		);

		product.setOwner(artist);
		product.setTags(savedTagEntities);
		product.setCategories(categoryEntities);
		product.setAttaches(new HashSet<>(attachEntitiesList));
		product = productRepository.save(product);

		ProductDetail productResponse = productMapper.entityToModelDetail(product);
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

		Set<MerchCategoryEntity> categoryEntities = categoryRepository.findAllByNameIn(new ArrayList<>(productModel.getCategories()));

		Set<MerchAttachEntity> attachEntities = merchAttachMapper.domainModelsToEntities(productModel.getAttaches());
		List<MerchAttachEntity> attachEntitiesList = attachRepository.saveAll(attachEntities);

		product.setTags(savedTagEntities);
		product.setCategories(categoryEntities);
		product.setAttaches(new HashSet<>(attachEntitiesList));
		product = productRepository.save(product);

		ProductDetail productResponse = productMapper.entityToModelDetail(product);
		return productResponse;
	}

	@Override
	public void delete(long id) {
		productRepository.delete(id, (byte) MerchStatus.NOT_AVAILABLE.getValue());
	}
}
