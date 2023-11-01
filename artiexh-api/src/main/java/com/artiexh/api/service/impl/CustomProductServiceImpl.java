package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.CustomProductService;
import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.CustomProductTagEntity;
import com.artiexh.data.jpa.entity.MediaEntity;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.ImageConfig;
import com.artiexh.data.jpa.repository.CustomProductRepository;
import com.artiexh.data.jpa.repository.CustomProductTagRepository;
import com.artiexh.data.jpa.repository.ProductVariantRepository;
import com.artiexh.model.domain.CustomProduct;
import com.artiexh.model.mapper.CustomProductMapper;
import com.artiexh.model.mapper.MediaMapper;
import com.artiexh.model.rest.customproduct.CustomProductDesignRequest;
import com.artiexh.model.rest.customproduct.CustomProductDesignResponse;
import com.artiexh.model.rest.customproduct.CustomProductGeneralRequest;
import com.artiexh.model.rest.customproduct.CustomProductGeneralResponse;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomProductServiceImpl implements CustomProductService {
	private final CustomProductRepository customProductRepository;
	private final ProductVariantRepository variantRepository;
	private final CustomProductTagRepository customProductTagRepository;
	private final CustomProductMapper customProductMapper;
	private final MediaMapper mediaMapper;

	@Override
	@Transactional
	public CustomProductGeneralResponse saveGeneral(CustomProductGeneralRequest item) {
		CustomProductEntity entity;

		if (item.getId() != null) {
			entity = updateGeneral(item);
		} else {
			entity = createGeneral(item);
		}

		return customProductMapper.entityToGeneralResponse(entity);
	}

	private CustomProductEntity createGeneral(CustomProductGeneralRequest item) {
		ProductVariantEntity variant = variantRepository.findById(item.getVariantId())
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.VARIANT_NOT_FOUND.getMessage() + item.getVariantId()));

		CustomProductEntity entity = customProductMapper.generalRequestToEntity(item);
		entity.setVariant(variant);
		entity.setCategory(variant.getProductTemplate().getCategory());

		var savedEntity = customProductRepository.save(entity);
		var savedTagEntities = saveCustomProductTag(savedEntity.getId(), item.getTags());
		savedEntity.setTags(new HashSet<>(savedTagEntities));

		return savedEntity;
	}

	private CustomProductEntity updateGeneral(CustomProductGeneralRequest item) {
		CustomProductEntity entity = customProductRepository.findById(item.getId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + item.getId()));

		if (!entity.getVariant().getId().equals(item.getVariantId())) {
			throw new IllegalArgumentException("Cannot change variant");
		}

		entity.setName(item.getName());
		entity.getImageSet().clear();
		if (item.getModelThumbnailId() != null) {
			entity.setModelThumbnail(MediaEntity.builder().id(item.getModelThumbnailId()).build());
		} else {
			entity.setModelThumbnail(null);
		}
		entity.setDescription(item.getDescription());
		entity.setMaxItemPerOrder(item.getMaxItemPerOrder());
		entity.getTags().clear();

		var savedEntity = customProductRepository.save(entity);
		var savedTagEntities = saveCustomProductTag(savedEntity.getId(), item.getTags());
		savedEntity.getTags().addAll(savedTagEntities);

		return savedEntity;
	}

	@Override
	@Transactional
	public CustomProductDesignResponse saveDesign(CustomProductDesignRequest item) {
		CustomProductEntity entity;

		if (item.getId() != null) {
			entity = updateDesign(item);
		} else {
			entity = createDesign(item);
		}

		return customProductMapper.entityToDesignResponse(entity);
	}

	private CustomProductEntity createDesign(CustomProductDesignRequest item) {
		ProductVariantEntity variant = variantRepository.findById(item.getVariantId())
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.VARIANT_NOT_FOUND.getMessage() + item.getVariantId()));

		validateImageSet(item.getCombinationCode(), item.getImageSet(), variant);

		CustomProductEntity entity = customProductMapper.designRequestToEntity(item);
		entity.setVariant(variant);
		entity.setCategory(variant.getProductTemplate().getCategory());

		return customProductRepository.save(entity);
	}

	private CustomProductEntity updateDesign(CustomProductDesignRequest item) {
		CustomProductEntity entity = customProductRepository.findById(item.getId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + item.getId()));

		if (!entity.getVariant().getId().equals(item.getVariantId())) {
			throw new IllegalArgumentException("Cannot change variant");
		}

		validateImageSet(item.getCombinationCode(), item.getImageSet(), entity.getVariant());

		entity.setName(item.getName());
		entity.setCombinationCode(item.getCombinationCode());

		entity.getImageSet().clear();
		if (item.getImageSet() != null) {
			entity.getImageSet().addAll(item.getImageSet().stream()
				.map(mediaMapper::detailToEntity)
				.collect(Collectors.toSet())
			);
		}

		if (item.getModelThumbnailId() != null) {
			entity.setModelThumbnail(MediaEntity.builder().id(item.getModelThumbnailId()).build());
		} else {
			entity.setModelThumbnail(null);
		}

		return customProductRepository.save(entity);
	}

	private List<CustomProductTagEntity> saveCustomProductTag(Long customProductId, Set<String> tags) {
		return customProductTagRepository.saveAll(
			tags.stream()
				.map(tag -> new CustomProductTagEntity(customProductId, tag))
				.collect(Collectors.toSet())
		);
	}

	private void validateImageSet(String combinationCode, Set<CustomProductDesignRequest.ImageSet> imageSet, ProductVariantEntity variantEntity) {
		if (combinationCode != null) {
			var combinationConfig = variantEntity.getProductTemplate().getImageCombinations().stream()
				.filter(combination -> combination.getCode().equals(combinationCode))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("combinationCode is not valid"));

			if (isNotValidImagePosition(combinationConfig, imageSet)) {
				throw new IllegalArgumentException("Image position is not valid");
			}
		} else if (imageSet != null && !imageSet.isEmpty()) {
			throw new IllegalArgumentException("Cannot set imageSet without combinationCode");
		}
	}

	private boolean isNotValidImagePosition(ImageCombination combinationConfig, Set<CustomProductDesignRequest.ImageSet> imageSet) {
		if (imageSet == null) {
			return false;
		}
		var configCodes = combinationConfig.getImages().stream().map(ImageConfig::getCode).collect(Collectors.toSet());
		var positionCodes = imageSet.stream().map(CustomProductDesignRequest.ImageSet::getPositionCode).collect(Collectors.toSet());
		positionCodes.removeAll(configCodes);
		return !positionCodes.isEmpty();
	}

	@Override
	public Page<CustomProduct> getAll(Specification<CustomProductEntity> specification, Pageable pageable) {
		Page<CustomProductEntity> itemPage = customProductRepository.findAll(specification, pageable);
		return itemPage.map(customProductMapper::entityToDomain);
	}

	@Override
	public CustomProductGeneralResponse getGeneralById(Long userId, Long id) {
		CustomProductEntity item = customProductRepository.findByIdAndArtistId(id, userId)
			.orElseThrow(EntityNotFoundException::new);
		return customProductMapper.entityToGeneralResponse(item);
	}

	@Override
	public CustomProductDesignResponse getDesignById(Long userId, Long id) {
		CustomProductEntity item = customProductRepository.findByIdAndArtistId(id, userId)
			.orElseThrow(EntityNotFoundException::new);
		return customProductMapper.entityToDesignResponse(item);
	}

	@Override
	public void delete(Long userId, Long id) {
		CustomProductEntity item = customProductRepository.findByIdAndArtistId(id, userId)
			.orElseThrow(EntityNotFoundException::new);
		customProductRepository.deleteById(item.getId());
	}
}
