package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.InventoryService;
import com.artiexh.data.jpa.entity.ImageSetEntity;
import com.artiexh.data.jpa.entity.InventoryItemEntity;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.repository.InventoryRepository;
import com.artiexh.data.jpa.repository.ProductVariantRepository;
import com.artiexh.model.domain.ImageSet;
import com.artiexh.model.domain.InventoryItem;
import com.artiexh.model.mapper.CycleAvoidingMappingContext;
import com.artiexh.model.mapper.InventoryMapper;
import com.artiexh.model.mapper.MediaMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
	private final InventoryRepository inventoryRepository;
	private final ProductVariantRepository variantRepository;
	private final InventoryMapper inventoryMapper;
	private final MediaMapper mediaMapper;
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

		return inventoryMapper.entityToDomain(entity, new CycleAvoidingMappingContext());
	}

	@Transactional
	public InventoryItemEntity createItem(InventoryItem item) {
		ProductVariantEntity variant = variantRepository.findById(item.getVariant().getId())
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + item.getVariant().getId()));

		InventoryItemEntity entity = inventoryMapper.domainToEntity(item);

		if (entity.getImageSet() != null && !entity.getImageSet().isEmpty()) {
			List<String> positionCode = item.getImageSet().stream().map(ImageSet::getPositionCode).toList();
			if (validateImagePosition(variant, item.getCombinationCode(), positionCode)) {
				throw new IllegalArgumentException("Image position is not valid");
			}

			Set<ImageSetEntity> savedImageSet = new HashSet<>(entity.getImageSet());
			entity.setImageSet(savedImageSet);

		} else {
			entity.setImageSet(null);
			entity.setCombinationCode(null);
		}

		entity.setVariant(variant);

		return inventoryRepository.save(entity);
	}

	@Transactional
	public InventoryItemEntity updateItem(InventoryItem item) {
		InventoryItemEntity entity = inventoryRepository.findById(item.getId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + item.getId()));

		if (item.getImageSet() != null && !item.getImageSet().isEmpty()) {
			ProductVariantEntity variant = variantRepository.findById(entity.getVariant().getId())
				.orElseThrow(() -> new IllegalArgumentException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + entity.getVariant().getId()));
			List<String> positionCode = item.getImageSet().stream().map(ImageSet::getPositionCode).toList();
			if (validateImagePosition(variant, item.getCombinationCode(), positionCode)) {
				throw new IllegalArgumentException("Image position is not valid");
			}

			entity.setCombinationCode(item.getCombinationCode());

			entity.getImageSet().clear();
			Set<ImageSetEntity> savedImageSet = new HashSet<>();
			for (ImageSet imageSet: item.getImageSet()) {
				ImageSetEntity imageSetEntity = mediaMapper.domainToEntity(imageSet);
				savedImageSet.add(imageSetEntity);
			}
			entity.getImageSet().addAll(savedImageSet);
		} else {
			entity.getImageSet().clear();
            entity.setCombinationCode(null);
		}

		return inventoryRepository.save(entity);
	}

	private boolean validateImagePosition(ProductVariantEntity variant, String combinationCode, List<String> positionCode) {
		if (StringUtils.isBlank(combinationCode)) {
			return true;
		}
		for (ImageCombination variantCombination : variant.getProductBase().getImageCombinations()) {
			if (variantCombination.getCode().equals(combinationCode)) {
				for (String code : positionCode) {
					if (variantCombination.getImages().stream().anyMatch(imageConfig -> imageConfig.getCode().equals(code))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InventoryItem> getAll(Specification<InventoryItemEntity> specification, Pageable pageable) {
		Page<InventoryItemEntity> itemPage = inventoryRepository.findAll(specification, pageable);
		return itemPage.map(item -> inventoryMapper.entityToDomain(item, new CycleAvoidingMappingContext()));
	}

	@Override
	@Transactional(readOnly = true)
	public InventoryItem getById(Long userId, Long id) {
		InventoryItemEntity item = inventoryRepository.findInventoryItemEntityByIdAndArtistId(id, userId)
			.orElseThrow(EntityNotFoundException::new);
		return inventoryMapper.entityToDomain(item, new CycleAvoidingMappingContext());
	}

	@Override
	public void delete(Long userId, Long id) {
		InventoryItemEntity item = inventoryRepository.findInventoryItemEntityByIdAndArtistId(id, userId)
			.orElseThrow(EntityNotFoundException::new);
		inventoryRepository.deleteById(item.getId());
	}
}
