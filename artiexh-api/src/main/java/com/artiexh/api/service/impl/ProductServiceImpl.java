package com.artiexh.api.service.impl;

import com.artiexh.api.service.ProductService;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.ProductCategoryEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductTagEntity;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.data.opensearch.model.ProductDocument;
import com.artiexh.model.domain.*;
import com.artiexh.model.mapper.AddressMapper;
import com.artiexh.model.mapper.ProductMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
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
	private final ElasticsearchOperations openSearchTemplate;
	private final AddressMapper addressMapper;

	@Override
	public Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable) {
		query.setPageable(pageable);
		SearchHits<ProductDocument> hits = openSearchTemplate.search(query, ProductDocument.class);
		SearchPage<ProductDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		return hitPage.map(searchHit -> ProductSuggestion.builder().name(searchHit.getContent().getName()).build());
	}

	@Override
	public Page<Product> getInPage(Query query, Pageable pageable) {
		query.setPageable(pageable);
		SearchHits<ProductDocument> hits = openSearchTemplate.search(query, ProductDocument.class);
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

			Shop shop = Shop.builder()
				.shopImageUrl(entity.getOwner().getShopImageUrl())
				.shopName(entity.getOwner().getShopName())
				.shopAddress(entity.getOwner().getShopAddress())
				.shopWard(addressMapper.wardEntityToWard(entity.getOwner().getShopWard()))
				.build();
			product.setShop(shop);
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

		Set<ProductEntity> bundleItems = getBundleItems(product);

		ProductEntity productEntity = productMapper.domainToEntity(product);
		productEntity.setOwner(artistEntity);
		productEntity.setShop(artistEntity);
		productEntity.setCategory(categoryEntity);
		productEntity.setTags(tagEntities);
		productEntity.setBundleItems(bundleItems);

		ProductEntity savedProductEntity;
		try {
			savedProductEntity = productRepository.save(productEntity);
			ProductDocument productDocument = productMapper.entityToDocument(savedProductEntity);
			openSearchTemplate.save(productDocument);
		} catch (Exception e) {
			log.error("Save product fail", e);
			throw e;
		}

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
		productEntity.setShop(ArtistEntity.builder().id(artistId).build());

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
		openSearchTemplate.update(productDocument);

		return productMapper.entityToDomain(savedProductEntity);
	}

	private Set<ProductEntity> getBundleItems(Product product) {
		if (ProductType.BUNDLE.equals(product.getType())) {
			if (product.getBundleItems() == null || product.getBundleItems().isEmpty()) {
				throw new IllegalArgumentException("Bundle must contain at least one item");
			}

			var itemEntities = productRepository.findAllByIdIn(
				product.getBundleItems().stream()
					.map(Product::getId)
					.collect(Collectors.toSet())
			);

			if (itemEntities.size() != product.getBundleItems().size()) {
				var existedIds = itemEntities.stream().map(ProductEntity::getId).collect(Collectors.toSet());
				var notExistedIds = product.getBundleItems().stream()
					.map(Product::getId)
					.filter(id -> !existedIds.contains(id))
					.map(String::valueOf)
					.collect(Collectors.joining(","));
				throw new IllegalArgumentException("Bundle item not valid: " + notExistedIds);
			}

			if (itemEntities.stream().anyMatch(item -> ProductType.BUNDLE.getByteValue() == item.getType())) {
				throw new IllegalArgumentException("Bundle item must not be bundle product");
			}

			return itemEntities;
		} else {
			if (product.getBundleItems() != null && !product.getBundleItems().isEmpty()) {
				throw new IllegalArgumentException("Only bundle product can contain bundleItems");
			}
			return null;
		}
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
