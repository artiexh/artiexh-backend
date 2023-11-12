package com.artiexh.api.service.productinventory.impl;

import com.artiexh.api.service.productinventory.ProductHistoryService;
import com.artiexh.api.service.productinventory.ProductInventoryJpaService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.ArtistRepository;
import com.artiexh.data.jpa.repository.ProductCategoryRepository;
import com.artiexh.data.jpa.repository.ProductInventoryRepository;
import com.artiexh.data.jpa.repository.ProductTagRepository;
import com.artiexh.model.domain.*;
import com.artiexh.model.mapper.ProductInventoryMapper;
import com.artiexh.model.rest.product.request.UpdateProductQuantitiesRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductInventoryJpaServiceImpl implements ProductInventoryJpaService {
	private final ProductInventoryRepository productRepository;
	private final ArtistRepository artistRepository;
	private final ProductCategoryRepository productCategoryRepository;
	private final ProductInventoryMapper productInventoryMapper;
	private final ProductTagRepository productTagRepository;
	private final ProductHistoryService productHistoryService;
	@Value("${artiexh.security.admin.id}")
	private Long rootAdminId;

	@Override
	public Page<ProductInventory> getInPage(Specification<ProductInventoryEntity> specification, Pageable pageable) {
		return productRepository.findAll(pageable).map(productInventoryMapper::entityToDomain);
	}

	@Override
	public ProductInventory getDetail(String productCode) {
		ProductInventoryEntity product = productRepository.findById(productCode).orElseThrow(EntityNotFoundException::new);
		return productInventoryMapper.entityToDomain(product);
	}

	@Override
	@Transactional
	public ProductInventory update(ProductInventory product) {
		ProductInventoryEntity productEntity = productRepository.findById(product.getProductCode()).orElseThrow(EntityNotFoundException::new);

		ProductCategoryEntity categoryEntity = productCategoryRepository.findById(product.getCategory().getId())
			.orElseThrow(() -> new IllegalArgumentException("Category not valid"));

		if (isManyThumbnail(product.getAttaches())) {
			throw new IllegalArgumentException("Only one thumbnail allowed");
		}

		Set<ProductTagEntity> tagEntities = getTagEntities(product.getTags());

		//Set<ProductInventoryEntity> bundleItems = getBundleItems(product);
		productEntity = productInventoryMapper.domainToEntity(product, productEntity);
		productEntity.setCategory(categoryEntity);
		productEntity.setTags(tagEntities);
		//productEntity.setBundleItems(bundleItems);

		ProductInventoryEntity savedProductEntity = productRepository.save(productEntity);

		product = productInventoryMapper.entityToDomain(savedProductEntity);
		return product;
	}

	@Override
	@Transactional
	public void updateQuantities(UpdateProductQuantitiesRequest request) {
		List<ProductInventoryEntity> productEntities = productRepository.findAllById(request.getProductQuantities().stream().map(ProductInventoryQuantity::getProductCode).collect(Collectors.toSet()));
		if (productEntities.size() != request.getProductQuantities().size()) {
			throw new IllegalStateException("Some products are not found");
		}
		for (ProductInventoryEntity productEntity : productEntities) {
			ProductInventoryQuantity inventoryQuantity = request.getProductQuantities().stream()
				.filter(productQuantity -> productQuantity.getProductCode().equals(productEntity.getProductCode()))
				.findFirst()
				.orElse(ProductInventoryQuantity.builder().build());
			if (request.getAction().equals(ProductHistoryAction.IMPORT)) {
				productEntity.setQuantity(productEntity.getQuantity() + inventoryQuantity.getQuantity());
			}
			if (request.getAction().equals(ProductHistoryAction.EXPORT)) {
				if (productEntity.getQuantity() < inventoryQuantity.getQuantity()) {
					throw new IllegalArgumentException("Product inventory is not enough to export");
				}
				productEntity.setQuantity(productEntity.getQuantity() - inventoryQuantity.getQuantity());
			}
		}

		productHistoryService.create(
			request.getAction(),
			request.getSourceId(),
			request.getSourceCategory(),
			request.getProductQuantities()
		);

		productRepository.saveAll(productEntities);
	}

	@Override
	@Transactional
	public ProductInventory create(Long id, ProductInventory product, ProductInCampaignEntity productInCampaign) {
		ArtistEntity artistEntity = artistRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Artist not valid"));

		ProductCategoryEntity categoryEntity = productCategoryRepository.findById(product.getCategory().getId())
			.orElseThrow(() -> new IllegalArgumentException("Category not valid"));

		if (isManyThumbnail(product.getAttaches())) {
			throw new IllegalArgumentException("Only one thumbnail allowed");
		}

		Set<ProductTagEntity> tagEntities = getTagEntities(product.getTags());

		//Set<ProductInventoryEntity> bundleItems = getBundleItems(product);

		ProductInventoryEntity productEntity = productInventoryMapper.domainToEntity(product);
		productEntity.setOwner(artistEntity);
		productEntity.setCategory(categoryEntity);
		productEntity.setTags(tagEntities);
		//productEntity.setBundleItems(bundleItems);

		ArtistEntity admin = artistRepository.findById(rootAdminId)
			.orElseThrow(() -> new IllegalArgumentException("Admin shop is not configured"));

		productEntity.setShop(admin);
		productEntity.setProductInCampaign(productInCampaign);

		ProductInventoryEntity savedProductEntity = productRepository.save(productEntity);

		product = productInventoryMapper.entityToDomain(savedProductEntity);
		return product;
	}

	@Override
	@Transactional
	public void updateQuantityFromCampaignRequest(Long sourceId, Set<ProductInventoryQuantity> productQuantities) {
		for (ProductInventoryQuantity productQuantity : productQuantities) {
			productRepository.updateQuantity(productQuantity.getProductCode(), productQuantity.getQuantity());
		}

		productHistoryService.create(ProductHistoryAction.IMPORT, sourceId, SourceCategory.CAMPAIGN_REQUEST, productQuantities);
	}

	private boolean isManyThumbnail(Set<ProductAttach> attaches) {
		return attaches.stream().filter(attach -> ProductAttachType.THUMBNAIL.equals(attach.getType())).count() > 1;
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
}
