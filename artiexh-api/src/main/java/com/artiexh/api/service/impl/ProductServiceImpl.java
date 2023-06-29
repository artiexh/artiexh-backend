package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductService;
import com.artiexh.data.elasticsearch.model.ProductDocument;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.ArtistRepository;
import com.artiexh.data.jpa.repository.ProductCategoryRepository;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.data.jpa.repository.ProductTagRepository;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductAttach;
import com.artiexh.model.domain.ProductTag;
import com.artiexh.model.mapper.ProductAttachMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductServiceImpl implements ProductService {
	private final ArtistRepository artistRepository;
	private final ProductCategoryRepository productCategoryRepository;
	private final ProductTagRepository productTagRepository;
	private final ProductMapper productMapper;
	private final ProductAttachMapper productAttachMapper;
	private final ProductRepository productRepository;
	private final ElasticsearchTemplate elasticsearchTemplate;

	@Override
	@Transactional
	public Page<Product> getInPage(Query query, Pageable pageable) {
		query.setPageable(pageable);
		SearchHits<ProductDocument> hits = elasticsearchTemplate.search(query, ProductDocument.class);
		SearchPage<ProductDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		var productPage = hitPage.map(searchHit -> productMapper.documentToDomain(searchHit.getContent()));

		// get missing fields from db: thumbnailUrl, remainingQuantity, owner.avatarUrl, description
		Set<Long> hitIds = hitPage.getSearchHits().stream()
			.map(searchHit -> searchHit.getContent().getId())
			.collect(Collectors.toSet());
		Map<Long, ProductEntity> entities = productRepository.findAllById(hitIds).stream()
			.collect(Collectors.toMap(ProductEntity::getId, product -> product));

		for (var product : productPage) {
			var entity = entities.get(product.getId());
			product.setAttaches(productAttachMapper.entitiesToDomains(entity.getAttaches()));
			product.setRemainingQuantity(entity.getRemainingQuantity());
			product.getOwner().setAvatarUrl(entity.getOwner().getAvatarUrl());
			product.setDescription(entity.getDescription());
		}

		return productPage;
	}

	@Override
	public Product getDetail(long id) {
		ProductEntity product = productRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);
		return productMapper.entityToDomain(product);
	}

	@Override
	public Product create(long userId, Product product) {

		ArtistEntity artistEntity = artistRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("Artist not valid"));

		ProductCategoryEntity categoryEntity = productCategoryRepository.findById(product.getCategory().getId())
			.orElseThrow(() -> new IllegalArgumentException("Category not valid"));

		Set<String> tagNames = product.getTags().stream().map(ProductTag::getName).collect(Collectors.toSet());
		Set<ProductTagEntity> tagEntities = productTagRepository.findAllByNameIn(tagNames);
		Set<String> existedTagNames = tagEntities.stream().map(ProductTagEntity::getName).collect(Collectors.toSet());
		tagEntities.addAll(
			tagNames.stream()
				.filter(tagName -> !existedTagNames.contains(tagName))
				.map(tagName -> ProductTagEntity.builder().name(tagName).build())
				.collect(Collectors.toSet())
		);

		ProductEntity productEntity = productMapper.domainToEntity(product);
		productEntity.setOwner(artistEntity);
		productEntity.setCategory(categoryEntity);
		productEntity.setTags(tagEntities);

		productRepository.save(productEntity);
		return productMapper.entityToDomain(productEntity);
	}

	@Override
	public ProductResponse update(Product product) {
		return null;

		/*ProductEntity product = productRepository.findById(productModel.getId()).orElseThrow(
			() -> new ResponseStatusException(PRODUCT_NOT_FOUND.getCode(), PRODUCT_NOT_FOUND.getMessage())
		);

		preCreateOrUpdate(productModel);

		Set<ProductTagEntity> tagEntities = productModel.getTags().stream()
			.map(tagName -> ProductTagEntity.builder()
				.name(tagName)
				.build())
			.collect(Collectors.toSet());
		Set<ProductTagEntity> savedTagEntities = tagRepository.findAllByNameIn(productModel.getTags());
		tagEntities.removeAll(savedTagEntities);
		savedTagEntities.addAll(tagRepository.saveAll(tagEntities));

		ProductCategoryEntity categoryEntity = categoryRepository.findById(productModel.getCategoryId())
			.orElseThrow(() -> new ResponseStatusException(CATEGORY_NOT_FOUND.getCode(), CATEGORY_NOT_FOUND.getMessage()));

		List<ProductAttachEntity> attachEntities = updateAttachment(productModel.getAttaches());

		PreOrderProductEntity preOrderProduct = preOrderProductRepository.findById(productModel.getId()).orElse(null);

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
				preOrderProductRepository.update(productModel.getStartDatetime(), productModel.getEndDateTime(), product.getId());
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

			preOrderProduct = preOrderProductRepository.save(preOrderProduct);

			productModel = productMapper.entityToModelDetail(preOrderProduct, productModel);
			productModel.setId(preOrderProduct.getId());
		}

		return productModel;*/
	}

	private void preCreateOrUpdate(Product productModel) {
		/*if (productModel.isPreorder() && (productModel.getStartDatetime() == null || productModel.getEndDateTime() == null)) {
			throw new ResponseStatusException(PREORDER_NOT_FOUND_TIME.getCode(), PREORDER_NOT_FOUND_TIME.getMessage());

		} else {
			if (productModel.isPreorder() && productModel.getEndDateTime().isBefore(productModel.getStartDatetime())) {
				throw new ResponseStatusException(PREORDER_INVALID_TIME.getCode(), PREORDER_INVALID_TIME.getMessage());
			}
		}*/
	}

	@Override
	public void delete(long userId, long productId) {
		productRepository.findById(productId)
			.ifPresentOrElse(
				product -> {
					if (!product.getOwner().getId().equals(userId)) {
						throw new IllegalArgumentException("This product is not own by user");
					}
					productRepository.softDelete(productId);
				},
				() -> {
					throw new IllegalArgumentException("Product not found");
				}
			);
	}

	private List<ProductAttachEntity> updateAttachment(Set<ProductAttach> attaches) {
		/*Set<Long> attachmentIds = attaches.stream()
			.map(ProductAttach::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		Set<ProductAttachEntity> updatedAttaches = attachRepository.findAllByIdIn(attachmentIds);
		if (attachmentIds.size() != updatedAttaches.size()) {
			throw new ResponseStatusException(ATTACHMENT_NOT_FOUND.getCode(), ATTACHMENT_NOT_FOUND.getMessage());
		}
		Set<ProductAttachEntity> attachEntities = productAttachMapper.domainModelsToEntities(attaches, updatedAttaches);
		return attachRepository.saveAll(attachEntities);*/
		return null;
	}
}
