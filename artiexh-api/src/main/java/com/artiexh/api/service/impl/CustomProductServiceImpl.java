package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.CustomProductService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.ImageConfig;
import com.artiexh.data.jpa.repository.CustomProductRepository;
import com.artiexh.data.jpa.repository.CustomProductTagRepository;
import com.artiexh.data.jpa.repository.MediaRepository;
import com.artiexh.data.jpa.repository.ProductVariantRepository;
import com.artiexh.model.mapper.CustomProductMapper;
import com.artiexh.model.mapper.MediaMapper;
import com.artiexh.model.rest.customproduct.*;
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
	private final MediaRepository mediaRepository;

	@Override
	@Transactional
	public CustomProductGeneralResponse createGeneral(CustomProductGeneralRequest item) {
		ProductVariantEntity variant = variantRepository.findById(item.getVariantId())
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.VARIANT_NOT_FOUND.getMessage() + item.getVariantId()));

		CustomProductEntity entity = customProductMapper.generalRequestToEntity(item);
		entity.setVariant(variant);
		entity.setCategory(variant.getProductTemplate().getCategory());

		var savedEntity = customProductRepository.save(entity);
		var savedTagEntities = saveCustomProductTag(savedEntity.getId(), item.getTags());
		savedEntity.setTags(new HashSet<>(savedTagEntities));

		return customProductMapper.entityToGeneralResponse(savedEntity);
	}

	@Override
	@Transactional
	public CustomProductGeneralResponse updateGeneral(CustomProductGeneralRequest item) {
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

		return customProductMapper.entityToGeneralResponse(savedEntity);
	}

	@Override
	@Transactional
	public CustomProductDesignResponse createDesign(CustomProductDesignRequest item) {
		ProductVariantEntity variant = variantRepository.findById(item.getVariantId())
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.VARIANT_NOT_FOUND.getMessage() + item.getVariantId()));

		var imageSetEntity = validateImageSet(
			item.getArtistId(),
			item.getCombinationCode(),
			item.getImageSet(),
			variant
		);

		CustomProductEntity entity = customProductMapper.designRequestToEntity(item);
		entity.setVariant(variant);
		entity.setCategory(variant.getProductTemplate().getCategory());
		entity.setImageSet(imageSetEntity);

		return customProductMapper.entityToDesignResponse(customProductRepository.save(entity));
	}

	@Override
	@Transactional
	public CustomProductDesignResponse updateDesign(CustomProductDesignRequest item) {
		CustomProductEntity entity = customProductRepository.findById(item.getId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage() + item.getId()));

		if (!entity.getVariant().getId().equals(item.getVariantId())) {
			throw new IllegalArgumentException("Cannot change variant");
		}

		var imageSetEntity = validateImageSet(
			item.getArtistId(),
			item.getCombinationCode(),
			item.getImageSet(),
			entity.getVariant()
		);

		entity.setName(item.getName());
		entity.setCombinationCode(item.getCombinationCode());

		entity.getImageSet().clear();
		if (item.getImageSet() != null) {
			entity.getImageSet().addAll(imageSetEntity);
		}

		if (item.getModelThumbnailId() != null) {
			entity.setModelThumbnail(MediaEntity.builder().id(item.getModelThumbnailId()).build());
		} else {
			entity.setModelThumbnail(null);
		}

		return customProductMapper.entityToDesignResponse(customProductRepository.save(entity));
	}

	private List<CustomProductTagEntity> saveCustomProductTag(Long customProductId, Set<String> tags) {
		return customProductTagRepository.saveAll(
			tags.stream()
				.map(tag -> new CustomProductTagEntity(customProductId, tag))
				.collect(Collectors.toSet())
		);
	}

	private Set<ImageSetEntity> validateImageSet(Long artistId,
												 String combinationCode,
												 Set<CustomProductDesignRequest.ImageSet> imageSet,
												 ProductVariantEntity variantEntity) {
		if (combinationCode != null) {
			var combinationConfig = variantEntity.getProductTemplate().getImageCombinations().stream()
				.filter(combination -> combination.getCode().equals(combinationCode))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("combinationCode is not valid"));

			if (isNotValidImagePosition(combinationConfig, imageSet)) {
				throw new IllegalArgumentException("Image position is not valid");
			}

			return validateImageSetMedia(artistId, imageSet);
		} else if (!imageSet.isEmpty()) {
			throw new IllegalArgumentException("Cannot set imageSet without combinationCode");
		}
		return Set.of();
	}

	private boolean isNotValidImagePosition(ImageCombination combinationConfig,
											Set<CustomProductDesignRequest.ImageSet> imageSet) {
		var configCodes = combinationConfig.getImages().stream().map(ImageConfig::getCode).collect(Collectors.toSet());
		var positionCodes = imageSet.stream()
			.map(CustomProductDesignRequest.ImageSet::getPositionCode)
			.collect(Collectors.toSet());
		positionCodes.removeAll(configCodes);
		return !positionCodes.isEmpty();
	}

	private Set<ImageSetEntity> validateImageSetMedia(Long artistId, Set<CustomProductDesignRequest.ImageSet> imageSets) {
		return imageSets.stream()
			.map(imageSet -> {
				var imageSetEntityBuilder = ImageSetEntity.builder()
					.positionCode(imageSet.getPositionCode())
					.mockupImage(mediaRepository.findByIdAndOwnerId(imageSet.getMockupImageId(), artistId)
						.orElseThrow(() ->
							new IllegalArgumentException("mockupImageId " + imageSet.getMockupImageId() + " is not valid")
						));

				if (imageSet.getManufacturingImageId() != null) {
					imageSetEntityBuilder.manufacturingImage(
						mediaRepository.findByIdAndOwnerId(imageSet.getManufacturingImageId(), artistId).orElseThrow(() ->
							new IllegalArgumentException("manufacturingImageId " + imageSet.getManufacturingImageId() + " is not valid")
						));
				}
				return imageSetEntityBuilder.build();
			})
			.collect(Collectors.toSet());
	}

	@Override
	public Page<CustomProductResponse> getAll(Specification<CustomProductEntity> specification, Pageable pageable) {
		Page<CustomProductEntity> itemPage = customProductRepository.findAll(specification, pageable);
		return itemPage.map(customProductMapper::entityToGetAllResponse);
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
