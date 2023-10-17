package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.InventoryItemService;
import com.artiexh.data.jpa.entity.InventoryItemEntity;
import com.artiexh.data.jpa.entity.InventoryItemTagEntity;
import com.artiexh.data.jpa.entity.MediaEntity;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.ImageConfig;
import com.artiexh.data.jpa.repository.InventoryItemRepository;
import com.artiexh.data.jpa.repository.InventoryItemTagRepository;
import com.artiexh.data.jpa.repository.MediaRepository;
import com.artiexh.data.jpa.repository.ProductVariantRepository;
import com.artiexh.model.domain.ImageSet;
import com.artiexh.model.domain.InventoryItem;
import com.artiexh.model.mapper.CycleAvoidingMappingContext;
import com.artiexh.model.mapper.InventoryMapper;
import com.artiexh.model.mapper.MediaMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryItemItemServiceImpl implements InventoryItemService {
	private final InventoryItemRepository inventoryItemRepository;
	private final ProductVariantRepository variantRepository;
	private final InventoryItemTagRepository inventoryItemTagRepository;
	private final InventoryMapper inventoryMapper;
	private final MediaMapper mediaMapper;
	private final MediaRepository mediaRepository;

	@Override
	@Transactional
	public InventoryItem save(InventoryItem item) {
		InventoryItemEntity entity;

		if (item.getId() != null) {
			entity = updateItem(item);
		} else {
			if (item.getVariant().getId() == null) {
				throw new IllegalArgumentException(ErrorCode.VARIANT_NOT_FOUND.getMessage());
			}
			entity = createItem(item);
		}

		return inventoryMapper.entityToDomain(entity);
	}

	@Transactional
	public InventoryItemEntity createItem(InventoryItem item) {
		ProductVariantEntity variant = variantRepository.findById(item.getVariant().getId())
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + item.getVariant().getId()));

		InventoryItemEntity entity = inventoryMapper.domainToEntity(item);

		if (entity.getCombinationCode() != null) {
			var combinationConfig = variant.getProductBase().getImageCombinations().stream()
				.filter(combination -> combination.getCode().equals(entity.getCombinationCode()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("combinationCode is not valid"));

			if (isNotValidateImagePosition(combinationConfig, item.getImageSet())) {
				throw new IllegalArgumentException("Image position is not valid");
			}
		} else if (entity.getImageSet() != null && !entity.getImageSet().isEmpty()) {
			throw new IllegalArgumentException("Cannot set imageSet without combinationCode");
		}

		entity.setVariant(variant);
		var savedEntity = inventoryItemRepository.save(entity);

		var savedTagEntities = saveInventoryItemTag(savedEntity.getId(), item.getTags());
		savedEntity.setTags(new HashSet<>(savedTagEntities));

		return savedEntity;
	}

	@Transactional
	public InventoryItemEntity updateItem(InventoryItem item) {
		InventoryItemEntity entity = inventoryItemRepository.findById(item.getId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + item.getId()));

		if (!entity.getVariant().getId().equals(item.getVariant().getId())) {
			throw new IllegalArgumentException("Cannot change variant");
		}

		if (item.getCombinationCode() != null) {
			var combinationConfig = entity.getVariant().getProductBase().getImageCombinations().stream()
				.filter(combination -> combination.getCode().equals(item.getCombinationCode()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("combinationCode is not valid"));

			if (isNotValidateImagePosition(combinationConfig, item.getImageSet())) {
				throw new IllegalArgumentException("Image position is not valid");
			}
		} else if (item.getImageSet() != null && !item.getImageSet().isEmpty()) {
			throw new IllegalArgumentException("Cannot set imageSet without combinationCode");
		}

		entity.setName(item.getName());
		entity.setCombinationCode(item.getCombinationCode());

		entity.getImageSet().clear();
		if (item.getImageSet() != null) {
			entity.getImageSet().addAll(item.getImageSet().stream()
				.map(mediaMapper::domainToEntity)
				.collect(Collectors.toSet())
			);
		}

		if (item.getThumbnail() != null) {
			entity.setThumbnail(mediaRepository.getReferenceById(item.getThumbnail().getId()));
		}

		entity.setDescription(item.getDescription());

		entity.getTags().clear();
		var savedEntity = inventoryItemRepository.saveAndFlush(entity);
		var savedTagEntities = saveInventoryItemTag(savedEntity.getId(), item.getTags());
		savedEntity.getTags().addAll(savedTagEntities);

		return savedEntity;
	}

	private List<InventoryItemTagEntity> saveInventoryItemTag(Long inventoryItemId, Set<String> tags) {
		return inventoryItemTagRepository.saveAll(
			tags.stream()
				.map(tag -> new InventoryItemTagEntity(inventoryItemId, tag))
				.collect(Collectors.toSet())
		);
	}

	private boolean isNotValidateImagePosition(ImageCombination combinationConfig, Set<ImageSet> imageSet) {
		if (imageSet == null) {
			return false;
		}
		var configCodes = combinationConfig.getImages().stream().map(ImageConfig::getCode).collect(Collectors.toSet());
		var positionCodes = imageSet.stream().map(ImageSet::getPositionCode).collect(Collectors.toSet());
		positionCodes.removeAll(configCodes);
		return !positionCodes.isEmpty();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InventoryItem> getAll(Specification<InventoryItemEntity> specification, Pageable pageable) {
		Page<InventoryItemEntity> itemPage = inventoryItemRepository.findAll(specification, pageable);
		return itemPage.map(inventoryMapper::entityToDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public InventoryItem getById(Long userId, Long id) {
		InventoryItemEntity item = inventoryItemRepository.findInventoryItemEntityByIdAndArtistId(id, userId)
			.orElseThrow(EntityNotFoundException::new);
		return inventoryMapper.entityToDomain(item);
	}

	@Override
	public void delete(Long userId, Long id) {
		InventoryItemEntity item = inventoryItemRepository.findInventoryItemEntityByIdAndArtistId(id, userId)
			.orElseThrow(EntityNotFoundException::new);
		inventoryItemRepository.deleteById(item.getId());
	}
}
