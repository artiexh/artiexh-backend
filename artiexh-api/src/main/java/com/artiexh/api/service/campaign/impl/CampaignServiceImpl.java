package com.artiexh.api.service.campaign.impl;

import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.data.jpa.entity.CustomProductTagEntity;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.CampaignStatus;
import com.artiexh.model.mapper.CampaignMapper;
import com.artiexh.model.mapper.CustomProductMapper;
import com.artiexh.model.rest.campaign.request.CreateCampaignRequest;
import com.artiexh.model.rest.campaign.response.CreateCampaignResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
		validateCreateCustomProductRequest(ownerId, request);

		var campaignEntity = campaignRepository.save(
			CampaignEntity.builder()
				.status(CampaignStatus.DRAFT.getByteValue())
				.owner(ArtistEntity.builder().id(ownerId).build())
				.providerId(request.getProviderId())
				.build()
		);

		var saveCustomProducts = request.getCustomProducts().stream()
			.map(customProductRequest -> {
				var inventoryItemEntity = inventoryItemRepository.getReferenceById(customProductRequest.getInventoryItemId());
				inventoryItemEntity.setIsLock(true);
				inventoryItemRepository.save(inventoryItemEntity);

				var customProductEntity = customProductMapper.createRequestToEntity(customProductRequest);
				customProductEntity.setInventoryItem(inventoryItemEntity);
				customProductEntity.setCampaign(campaignEntity);
				var savedCustomProductEntity = customProductRepository.save(customProductEntity);

				var savedCustomProductTag = new HashSet<>(customProductTagRepository.saveAll(
					customProductRequest.getTags().stream()
						.map(tag -> {
							var customProductTagEntity = new CustomProductTagEntity();
							customProductTagEntity.setCustomProductId(savedCustomProductEntity.getId());
							customProductTagEntity.setName(tag);
							return customProductTagEntity;
						})
						.collect(Collectors.toSet())
				));

				savedCustomProductEntity.setTags(savedCustomProductTag);
				return customProductEntity;
			})
			.collect(Collectors.toSet());


		campaignEntity.setCustomProducts(saveCustomProducts);
		return campaignMapper.entityToResponse(campaignEntity);
	}

	@Override
	public Page<CreateCampaignResponse> getAllCampaigns(Specification<CampaignEntity> specification, Pageable pageable) {
		return campaignRepository.findAll(specification, pageable)
			.map(campaignMapper::entityToResponse);
	}

	private void validateCreateCustomProductRequest(Long ownerId, CreateCampaignRequest request) {
		if (!providerRepository.existsById(request.getProviderId())) {
			throw new IllegalArgumentException("providerId " + request.getProviderId() + " not valid");
		}

		for (var customProductRequest : request.getCustomProducts()) {
			if (!productCategoryRepository.existsById(customProductRequest.getProductCategoryId())) {
				throw new IllegalArgumentException("productCategory " + customProductRequest.getProductCategoryId() + " not valid");
			}

			var inventoryItemEntity = inventoryItemRepository.findById(customProductRequest.getInventoryItemId())
				.orElseThrow(() -> new IllegalArgumentException("inventoryItem " + customProductRequest.getInventoryItemId() + " not valid"));

			if (!ownerId.equals(inventoryItemEntity.getArtist().getId())) {
				throw new IllegalArgumentException("you not own inventoryItem " + customProductRequest.getInventoryItemId());
			}

			if (Boolean.TRUE.equals(inventoryItemEntity.getIsLock())) {
				throw new IllegalArgumentException("inventoryItem " + customProductRequest.getInventoryItemId() + " is locked");
			}

			var providerConfig = inventoryItemEntity.getVariant().getProviderConfigs().stream()
				.filter(config ->
					config.getId().getBusinessCode().equals(request.getProviderId())
				)
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("inventoryItem " + customProductRequest.getInventoryItemId() + " is not supported by provider " + request.getProviderId()));

			if (providerConfig.getMinQuantity() > customProductRequest.getQuantity()) {
				throw new IllegalArgumentException("customProduct quantity must greater than " + providerConfig.getMinQuantity());
			}

			if (providerConfig.getBasePriceAmount().compareTo(customProductRequest.getPrice().getAmount()) > 0) {
				throw new IllegalArgumentException("customProduct price amount must greater than " + providerConfig.getBasePriceAmount());
			}
		}
	}

}
