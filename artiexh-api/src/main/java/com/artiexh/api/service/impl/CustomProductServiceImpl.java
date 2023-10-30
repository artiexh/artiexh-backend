package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.CustomProductService;
import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.CustomProductTagEntity;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.ImageConfig;
import com.artiexh.data.jpa.repository.CustomProductRepository;
import com.artiexh.data.jpa.repository.CustomProductTagRepository;
import com.artiexh.data.jpa.repository.MediaRepository;
import com.artiexh.data.jpa.repository.ProductVariantRepository;
import com.artiexh.model.domain.CustomProduct;
import com.artiexh.model.domain.ImageSet;
import com.artiexh.model.mapper.CustomProductMapper;
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
public class CustomProductServiceImpl implements CustomProductService {
	private final CustomProductRepository customProductRepository;
	private final ProductVariantRepository variantRepository;
	private final CustomProductTagRepository customProductTagRepository;
	private final CustomProductMapper customProductMapper;
	private final MediaMapper mediaMapper;
	private final MediaRepository mediaRepository;

	@Override
	@Transactional
	public CustomProduct save(CustomProduct item) {
		CustomProductEntity entity;

		if (item.getId() != null) {
			entity = updateItem(item);
		} else {
			if (item.getVariant().getId() == null) {
				throw new IllegalArgumentException(ErrorCode.VARIANT_NOT_FOUND.getMessage());
			}
			entity = createItem(item);
		}

		return customProductMapper.entityToDomain(entity);
	}

	@Transactional
	public CustomProductEntity createItem(CustomProduct item) {
		ProductVariantEntity variant = variantRepository.findById(item.getVariant().getId())
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + item.getVariant().getId()));

		CustomProductEntity entity = customProductMapper.domainToEntity(item);

		if (entity.getCombinationCode() != null) {
			var combinationConfig = variant.getProductTemplate().getImageCombinations().stream()
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
		var savedEntity = customProductRepository.save(entity);

		var savedTagEntities = saveCustomProductTag(savedEntity.getId(), item.getTags());
		savedEntity.setTags(new HashSet<>(savedTagEntities));

		return savedEntity;
	}

	@Transactional
	public CustomProductEntity updateItem(CustomProduct item) {
		CustomProductEntity entity = customProductRepository.findById(item.getId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + item.getId()));

		if (!entity.getVariant().getId().equals(item.getVariant().getId())) {
			throw new IllegalArgumentException("Cannot change variant");
		}

		if (item.getCombinationCode() != null) {
			var combinationConfig = entity.getVariant().getProductTemplate().getImageCombinations().stream()
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
		var savedEntity = customProductRepository.saveAndFlush(entity);
		var savedTagEntities = saveCustomProductTag(savedEntity.getId(), item.getTags());
		savedEntity.getTags().addAll(savedTagEntities);

		return savedEntity;
	}

	private List<CustomProductTagEntity> saveCustomProductTag(Long inventoryItemId, Set<String> tags) {
		return customProductTagRepository.saveAll(
			tags.stream()
				.map(tag -> new CustomProductTagEntity(inventoryItemId, tag))
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
	public Page<CustomProduct> getAll(Specification<CustomProductEntity> specification, Pageable pageable) {
		Page<CustomProductEntity> itemPage = customProductRepository.findAll(specification, pageable);
		return itemPage.map(customProductMapper::entityToDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public CustomProduct getById(Long userId, Long id) {
		CustomProductEntity item = customProductRepository.findInventoryItemEntityByIdAndArtistId(id, userId)
			.orElseThrow(EntityNotFoundException::new);
		return customProductMapper.entityToDomain(item);
	}

	@Override
	public void delete(Long userId, Long id) {
		CustomProductEntity item = customProductRepository.findInventoryItemEntityByIdAndArtistId(id, userId)
			.orElseThrow(EntityNotFoundException::new);
		customProductRepository.deleteById(item.getId());
	}
}
