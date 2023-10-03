package com.artiexh.api.service.campaign.impl;

import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.CustomProductTagEntity;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.CampaignStatus;
import com.artiexh.model.mapper.CampaignMapper;
import com.artiexh.model.mapper.CustomProductMapper;
import com.artiexh.model.rest.campaign.request.CreateCampaignRequest;
import com.artiexh.model.rest.campaign.request.CreateCustomProductRequest;
import com.artiexh.model.rest.campaign.request.UpdateCampaignRequest;
import com.artiexh.model.rest.campaign.response.CreateCampaignResponse;
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
public class CampaignServiceImpl implements CampaignService {
	private final CampaignRepository campaignRepository;
	private final CustomProductRepository customProductRepository;
	private final InventoryItemRepository inventoryItemRepository;
	private final ProviderRepository providerRepository;
	private final ProductCategoryRepository productCategoryRepository;
	private final CustomProductTagRepository customProductTagRepository;
	private final CustomProductMapper customProductMapper;
	private final CampaignMapper campaignMapper;

	@Override
	@Transactional
	public CreateCampaignResponse createCampaign(Long ownerId, CreateCampaignRequest request) {
		validateCreateCustomProductRequest(ownerId, request.getProviderId(), CampaignStatus.DRAFT, request.getCustomProducts());

		var campaignEntity = campaignRepository.save(
			CampaignEntity.builder()
				.status(CampaignStatus.DRAFT.getByteValue())
				.owner(ArtistEntity.builder().id(ownerId).build())
				.providerId(request.getProviderId())
				.build()
		);

		var saveCustomProducts = request.getCustomProducts().stream().map(customProductRequest -> {
			var inventoryItemEntity = inventoryItemRepository.getReferenceById(customProductRequest.getInventoryItemId());
			inventoryItemEntity.setIsLock(true);
			inventoryItemRepository.save(inventoryItemEntity);

			var customProductEntity = customProductMapper.createRequestToEntity(customProductRequest);
			customProductEntity.setInventoryItem(inventoryItemEntity);
			customProductEntity.setCampaign(campaignEntity);
			var savedCustomProductEntity = customProductRepository.save(customProductEntity);

			var savedCustomProductTag = saveCustomProductTag(savedCustomProductEntity.getId(), customProductRequest.getTags());
			savedCustomProductEntity.setTags(new HashSet<>(savedCustomProductTag));
			return savedCustomProductEntity;
		}).collect(Collectors.toSet());


		campaignEntity.setCustomProducts(saveCustomProducts);
		return campaignMapper.entityToResponse(campaignEntity);
	}

	@Override
	@Transactional
	public CreateCampaignResponse updateCampaign(Long ownerId, UpdateCampaignRequest request) {
		var oldCampaignEntity = campaignRepository.findById(request.getId())
			.orElseThrow(() -> new EntityNotFoundException("campaignId " + request.getId() + " not valid"));

		if (!oldCampaignEntity.getOwner().getId().equals(ownerId)) {
			throw new IllegalArgumentException("You not own campaign " + request.getId());
		}

		if (!oldCampaignEntity.getStatus().equals(CampaignStatus.DRAFT.getByteValue()) &&
			!oldCampaignEntity.getStatus().equals(CampaignStatus.REQUEST_CHANGE.getByteValue())) {
			throw new IllegalArgumentException("You can only update campaign with status DRAFT or REQUEST_CHANGE");
		}

		validateCreateCustomProductRequest(ownerId,
			request.getProviderId(),
			CampaignStatus.fromValue(oldCampaignEntity.getStatus()),
			request.getCustomProducts()
		);

		var inventoryItemToCustomProductMap = oldCampaignEntity.getCustomProducts().stream()
			.collect(Collectors.toMap(
				customProductEntity -> customProductEntity.getInventoryItem().getId(),
				customProductEntity -> customProductEntity)
			);

		var saveCustomProducts = request.getCustomProducts().stream()
			.map(customProductRequest -> {
				CustomProductEntity customProductEntity = inventoryItemToCustomProductMap.get(customProductRequest.getInventoryItemId());
				if (customProductEntity != null) {
					customProductMapper.createRequestToEntity(customProductRequest, customProductEntity);
				} else {
					var inventoryItemEntity = inventoryItemRepository.getReferenceById(customProductRequest.getInventoryItemId());
					inventoryItemEntity.setIsLock(true);
					inventoryItemRepository.save(inventoryItemEntity);

					customProductEntity = customProductMapper.createRequestToEntity(customProductRequest);
					customProductEntity.setInventoryItem(inventoryItemEntity);
					customProductEntity.setCampaign(oldCampaignEntity);
				}

				customProductEntity.getTags().clear();
				var savedCustomProductEntity = customProductRepository.save(customProductEntity);

				var savedCustomProductTag = saveCustomProductTag(savedCustomProductEntity.getId(), customProductRequest.getTags());
				savedCustomProductEntity.getTags().addAll(savedCustomProductTag);
				return savedCustomProductEntity;
			})
			.collect(Collectors.toSet());

		oldCampaignEntity.getCustomProducts().clear();
		oldCampaignEntity.getCustomProducts().addAll(saveCustomProducts);
		var savedCampaignEntity = campaignRepository.save(oldCampaignEntity);
		return campaignMapper.entityToResponse(savedCampaignEntity);
	}

	private List<CustomProductTagEntity> saveCustomProductTag(Long customProductId, Set<String> tags) {
		return customProductTagRepository.saveAll(
			tags.stream().map(tag -> {
				var customProductTagEntity = new CustomProductTagEntity();
				customProductTagEntity.setCustomProductId(customProductId);
				customProductTagEntity.setName(tag);
				return customProductTagEntity;
			}).collect(Collectors.toSet()));
	}

	private void validateCreateCustomProductRequest(Long ownerId,
													String providerId,
													CampaignStatus status,
													Set<? extends CreateCustomProductRequest> requests) {
		if (!providerRepository.existsById(providerId)) {
			throw new IllegalArgumentException("providerId " + providerId + " not valid");
		}

		for (var customProductRequest : requests) {
			if (!productCategoryRepository.existsById(customProductRequest.getProductCategoryId())) {
				throw new IllegalArgumentException("productCategory " + customProductRequest.getProductCategoryId() + " not valid");
			}

			var inventoryItemEntity = inventoryItemRepository.findById(customProductRequest.getInventoryItemId())
				.orElseThrow(() -> new IllegalArgumentException("inventoryItem " + customProductRequest.getInventoryItemId() + " not valid"));

			if (!ownerId.equals(inventoryItemEntity.getArtist().getId())) {
				throw new IllegalArgumentException("you not own inventoryItem " + customProductRequest.getInventoryItemId());
			}

			if (Boolean.TRUE.equals(inventoryItemEntity.getIsLock())) {
				if (status != CampaignStatus.DRAFT && status != CampaignStatus.REQUEST_CHANGE) {
					throw new IllegalArgumentException("inventoryItem " + customProductRequest.getInventoryItemId() + " is locked");
				}
			}

			var providerConfig = inventoryItemEntity.getVariant().getProviderConfigs().stream()
				.filter(config -> config.getId().getBusinessCode().equals(providerId))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("inventoryItem " + customProductRequest.getInventoryItemId() + " is not supported by provider " + providerId));

			if (providerConfig.getMinQuantity() > customProductRequest.getQuantity()) {
				throw new IllegalArgumentException("customProduct quantity must greater than " + providerConfig.getMinQuantity());
			}

			if (providerConfig.getBasePriceAmount().compareTo(customProductRequest.getPrice().getAmount()) > 0) {
				throw new IllegalArgumentException("customProduct price amount must greater than " + providerConfig.getBasePriceAmount());
			}
		}
	}

	@Override
	public Page<CreateCampaignResponse> getAllCampaigns(Specification<CampaignEntity> specification, Pageable pageable) {
		return campaignRepository.findAll(specification, pageable).map(campaignMapper::entityToResponse);
	}
}
