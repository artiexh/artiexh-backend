package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductService;
import com.artiexh.data.elasticsearch.model.ProductDocument;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.MerchAttach;
import com.artiexh.model.domain.MerchAttachType;
import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.mapper.MerchAttachMapper;
import com.artiexh.model.mapper.MerchMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.product.ProductDetail;
import com.artiexh.model.rest.product.response.ProductInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static com.artiexh.api.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {
	private final ProductMapper productMapper;
	private final MerchAttachMapper merchAttachMapper;
	private final MerchMapper merchMapper;
	private final MerchAttachRepository attachRepository;
	private final ProductRepository productRepository;
	private final PreOrderMerchRepository preOrderMerchRepository;
	private final MerchCategoryRepository categoryRepository;
	private final MerchTagRepository tagRepository;
	private final ArtistRepository artistRepository;
	private final ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public ProductDetail getDetail(long id) {
		MerchEntity merch = productRepository.findById(id).orElseThrow(
			() -> new ResponseStatusException(PRODUCT_NOT_FOUND.getCode(), PRODUCT_NOT_FOUND.getMessage())
		);
		return productMapper.entityToModelDetail(merch);
	}

	@Override
	public PageResponse<ProductInfoResponse> getInPage(Criteria criteria, Pageable pageable) {
		Query query = new CriteriaQuery(criteria, pageable);
		SearchHits<ProductDocument> hits = elasticsearchTemplate.search(query, ProductDocument.class);
		SearchPage<ProductDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		var result = hitPage.map(searchHit -> merchMapper.documentToProductInfoResponse(searchHit.getContent()));

		// get missing fields from db: thumbnailUrl, currency, remainingQuantity, owner.avatarUrl
		Set<Long> hitIds = hitPage.getSearchHits().stream()
			.map(searchHit -> searchHit.getContent().getId())
			.collect(Collectors.toSet());
		Map<Long, MerchEntity> entities = productRepository.findAllById(hitIds).stream()
			.collect(Collectors.toMap(MerchEntity::getId, merch -> merch));

		for (var productInfoResponse : result) {
			var entity = entities.get(productInfoResponse.getId());
			entity.getAttaches().stream()
				.filter(attach -> attach.getType() == MerchAttachType.THUMBNAIL.getValue())
				.findAny()
				.ifPresent(thumbnail -> productInfoResponse.setThumbnailUrl(thumbnail.getUrl()));
			productInfoResponse.setCurrency(entity.getCurrency());
			productInfoResponse.setRemainingQuantity(entity.getRemainingQuantity());
			productInfoResponse.getOwner().setAvatarUrl(entity.getOwner().getAvatarUrl());
		}

		return new PageResponse<>(result);
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
		Set<MerchTagEntity> savedTagEntities = tagRepository.findAllByNameIn(productModel.getTags());
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
		Set<MerchTagEntity> savedTagEntities = tagRepository.findAllByNameIn(productModel.getTags());
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
		Set<Long> attachmentIds = attaches.stream()
			.map(MerchAttach::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		Set<MerchAttachEntity> updatedAttaches = attachRepository.findAllByIdIn(attachmentIds);
		if (attachmentIds.size() != updatedAttaches.size()) {
			throw new ResponseStatusException(ATTACHMENT_NOT_FOUND.getCode(), ATTACHMENT_NOT_FOUND.getMessage());
		}
		Set<MerchAttachEntity> attachEntities = merchAttachMapper.domainModelsToEntities(attaches, updatedAttaches);
		return attachRepository.saveAll(attachEntities);
	}
}
