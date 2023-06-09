package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductService;
import com.artiexh.data.elasticsearch.model.ProductDocument;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.ProductCategoryEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductTagEntity;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductAttach;
import com.artiexh.model.domain.ProductAttachType;
import com.artiexh.model.domain.ProductTag;
import com.artiexh.model.mapper.ProductMapper;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private final ProductAttachRepository productAttachRepository;
	private final ProductTagRepository productTagRepository;
	private final ProductMapper productMapper;
	private final ProductRepository productRepository;
	private final ElasticsearchTemplate elasticsearchTemplate;

	@Override
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
			String thumbnailUrl = productAttachRepository.findThumbnailByProductId(product.getId()).orElse(null);
			product.setThumbnailUrl(thumbnailUrl);

			var entity = entities.get(product.getId());
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
	@Transactional
	public Product create(long artistId, Product product) {

		ArtistEntity artistEntity = artistRepository.findById(artistId)
			.orElseThrow(() -> new IllegalArgumentException("Artist not valid"));

		ProductCategoryEntity categoryEntity = productCategoryRepository.findById(product.getCategory().getId())
			.orElseThrow(() -> new IllegalArgumentException("Category not valid"));

		if (isManyThumbnail(product.getAttaches())) {
			throw new IllegalArgumentException("Only one thumbnail allowed");
		}

		Set<ProductTagEntity> tagEntities = getTagEntities(product.getTags());

		ProductEntity productEntity = productMapper.domainToEntity(product);
		productEntity.setOwner(artistEntity);
		productEntity.setCategory(categoryEntity);
		productEntity.setTags(tagEntities);

		ProductEntity savedProductEntity;
		try {
			savedProductEntity = productRepository.save(productEntity);
		} catch (Exception e) {
			log.error("Save product fail", e);
			throw e;
		}

		ProductDocument productDocument = productMapper.entityToDocument(savedProductEntity);
		elasticsearchTemplate.save(productDocument);

		return productMapper.entityToDomain(savedProductEntity);
	}

	@Override
	@Transactional
	public Product update(long artistId, Product product) {
		ProductEntity savedProduct = productRepository.findById(product.getId())
			.orElseThrow(() -> new EntityNotFoundException("Product id not found"));

		if (savedProduct.getOwner().getId() != artistId) {
			throw new AccessDeniedException("Product owner not valid");
		}

		if (isManyThumbnail(product.getAttaches())) {
			throw new IllegalArgumentException("Only one thumbnail allowed");
		}

		ProductEntity productEntity = productMapper.domainToEntity(product);
		productEntity.setOwner(ArtistEntity.builder().id(artistId).build());

		productEntity.setTags(getTagEntities(product.getTags()));
		productEntity.setCategory(productCategoryRepository.findById(product.getCategory().getId())
			.orElseThrow(() -> new IllegalArgumentException("Category not valid"))
		);

		ProductEntity savedProductEntity;
		try {
			savedProductEntity = productRepository.save(productEntity);
		} catch (Exception e) {
			log.error("Save product fail", e);
			throw e;
		}

		ProductDocument productDocument = productMapper.entityToDocument(savedProductEntity);
		elasticsearchTemplate.update(productDocument);

		return productMapper.entityToDomain(savedProductEntity);
	}

	private Set<ProductTagEntity> getTagEntities(Set<ProductTag> tags) {
		Set<String> tagNames = tags.stream().map(ProductTag::getName).collect(Collectors.toSet());
		Set<ProductTagEntity> tagEntities = productTagRepository.findAllByNameIn(tagNames);
		Set<String> existedTagNames = tagEntities.stream().map(ProductTagEntity::getName).collect(Collectors.toSet());
		tagEntities.addAll(
			tagNames.stream()
				.filter(tagName -> !existedTagNames.contains(tagName))
				.map(tagName -> ProductTagEntity.builder().name(tagName).build())
				.collect(Collectors.toSet())
		);
		return tagEntities;
	}

	private boolean isManyThumbnail(Set<ProductAttach> attaches) {
		return attaches.stream().filter(attach -> ProductAttachType.THUMBNAIL.equals(attach.getType())).count() > 1;
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
}
