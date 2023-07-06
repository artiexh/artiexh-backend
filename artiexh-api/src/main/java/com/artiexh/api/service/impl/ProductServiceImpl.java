package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductService;
import com.artiexh.data.elasticsearch.model.ProductDocument;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.ProductCategoryEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductTagEntity;
import com.artiexh.data.jpa.repository.ArtistRepository;
import com.artiexh.data.jpa.repository.ProductCategoryRepository;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.data.jpa.repository.ProductTagRepository;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductTag;
import com.artiexh.model.mapper.ProductAttachMapper;
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
	@Transactional
	public Product create(long userId, Product product) {

		ArtistEntity artistEntity = artistRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("Artist not valid"));

		ProductCategoryEntity categoryEntity = productCategoryRepository.findById(product.getCategory().getId())
			.orElseThrow(() -> new IllegalArgumentException("Category not valid"));

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
	public Product update(Product product) {
		if (productRepository.existsById(product.getId())) {
			throw new EntityNotFoundException("Product id not found");
		}

		ProductEntity productEntity = productMapper.domainToEntity(product);

		productEntity.setTags(getTagEntities(product.getTags()));
		productEntity.setCategory(productCategoryRepository.findById(product.getCategory().getId())
			.orElseThrow(() -> new IllegalArgumentException("Category not valid"))
		);

		ProductEntity savedProductEntity = productRepository.save(productEntity);
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
