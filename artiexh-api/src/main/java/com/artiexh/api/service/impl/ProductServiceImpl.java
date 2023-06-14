package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.domain.MerchAttach;
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
	private final PreOrderMerchRepository preOrderMerchRepository;
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
		Page<ProductInfo> productPage = products.map(productMapper::entityToModelInfo);
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

		preCreateOrUpdate(productModel);

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

		if (!productModel.isPreorder()) {
			product = productRepository.save(product);

			productModel = productMapper.entityToModelDetail(product, productModel);
			productModel.setId(product.getId());
		} else {
			PreOrderMerchEntity preOrderProduct = PreOrderMerchEntity.parentBuilder(product).build();
			preOrderProduct.setStartDatetime(productModel.getStartDatetime());
			preOrderProduct.setEndDatetime(productModel.getEndDateTime());

			preOrderProduct = preOrderMerchRepository.save(preOrderProduct);

			productModel = productMapper.entityToModelDetail(preOrderProduct, productModel);
			productModel.setId(preOrderProduct.getId());
		}

		return productModel;
	}

	@Override
	public ProductDetail update(ProductDetail productModel) {

		MerchEntity product = productRepository.findById(productModel.getId()).orElseThrow(
			() -> new ResponseStatusException(PRODUCT_NOT_FOUND.getCode(), PRODUCT_NOT_FOUND.getMessage())
		);

		preCreateOrUpdate(productModel);

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

		List<MerchAttachEntity> attachEntities = updateAttachment(productModel.getAttaches());


		PreOrderMerchEntity preOrderProduct = preOrderMerchRepository.findById(productModel.getId()).orElse(null);

		if (preOrderProduct == null) {
			product = productMapper.domainModelToEntity(productModel, product);
			product.setTags(savedTagEntities);
			product.setCategory(categoryEntity);
			product.getAttaches().clear();
			product.getAttaches().addAll(attachEntities);

			product = productRepository.save(product);

			productModel = productMapper.entityToModelDetail(product, productModel);
			productModel.setId(product.getId());
			if (productModel.isPreorder()) {
				preOrderMerchRepository.update(productModel.getStartDatetime(), productModel.getEndDateTime(), product.getId());
			}
		} else {

			preOrderProduct = productMapper.domainModelToEntity(productModel, preOrderProduct);

			preOrderProduct.setStartDatetime(productModel.getStartDatetime());
			preOrderProduct.setEndDatetime(productModel.getEndDateTime());
			preOrderProduct.getTags().clear();
			preOrderProduct.getTags().addAll(savedTagEntities);
			preOrderProduct.setCategory(categoryEntity);
			preOrderProduct.getAttaches().clear();
			preOrderProduct.getAttaches().addAll(attachEntities);

			preOrderProduct = preOrderMerchRepository.save(preOrderProduct);

			productModel = productMapper.entityToModelDetail(preOrderProduct, productModel);
			productModel.setId(preOrderProduct.getId());
		}

		return productModel;
	}

	private void preCreateOrUpdate(ProductDetail productModel) {
		if (productModel.isPreorder() && (productModel.getStartDatetime() == null || productModel.getEndDateTime() == null)) {
			throw new ResponseStatusException(PREORDER_NOT_FOUND_TIME.getCode(), PREORDER_NOT_FOUND_TIME.getMessage());

		} else {
			if (productModel.isPreorder() && productModel.getEndDateTime().isBefore(productModel.getStartDatetime())) {
				throw new ResponseStatusException(PREORDER_INVALID_TIME.getCode(), PREORDER_INVALID_TIME.getMessage());
			}
		}
	}

	@Override
	public void delete(long id) {
		productRepository.delete(id, (byte) MerchStatus.NOT_AVAILABLE.getValue());
	}

	private List<MerchAttachEntity> updateAttachment(Set<MerchAttach> attaches) {
		List<Long> attachmentIds = attaches.stream()
			.filter(attachment -> attachment.getId() != null)
			.map(MerchAttach::getId)
			.toList();
		Set<MerchAttachEntity> updatedAttaches = new HashSet<>(attachRepository.findAllById(attachmentIds));
		Set<MerchAttachEntity> attachEntities = merchAttachMapper.domainModelsToEntities(attaches, updatedAttaches);
		return attachRepository.saveAll(attachEntities);
	}
}
