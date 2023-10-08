package com.artiexh.api.service.campaign.impl;

import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.CampaignHistoryAction;
import com.artiexh.model.domain.CampaignStatus;
import com.artiexh.model.domain.Role;
import com.artiexh.model.mapper.CampaignMapper;
import com.artiexh.model.mapper.CustomProductMapper;
import com.artiexh.model.rest.campaign.request.CampaignRequest;
import com.artiexh.model.rest.campaign.request.CustomProductRequest;
import com.artiexh.model.rest.campaign.request.UpdateCampaignStatusRequest;
import com.artiexh.model.rest.campaign.response.CampaignDetailResponse;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
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
	private final CustomProductTagRepository customProductTagRepository;
	private final CampaignHistoryRepository campaignHistoryRepository;
	private final AccountRepository accountRepository;
	private final CustomProductMapper customProductMapper;
	private final CampaignMapper campaignMapper;

	@Override
	@Transactional
	public CampaignDetailResponse createCampaign(Long ownerId, CampaignRequest request) {
		validateCreateCustomProductRequest(ownerId, request.getProviderId(), request.getCustomProducts());

		var campaignEntity = campaignRepository.save(
			CampaignEntity.builder()
				.status(CampaignStatus.DRAFT.getByteValue())
				.owner(ArtistEntity.builder().id(ownerId).build())
				.providerId(request.getProviderId())
				.name(request.getName())
				.description(request.getDescription())
				.build()
		);

		var saveCustomProducts = request.getCustomProducts().stream().map(customProductRequest -> {
			var inventoryItemEntity = inventoryItemRepository.getReferenceById(customProductRequest.getInventoryItemId());

			var customProductEntity = customProductMapper.createRequestToEntity(customProductRequest);
			customProductEntity.setInventoryItem(inventoryItemEntity);
			customProductEntity.setCampaign(campaignEntity);
			if (!StringUtils.hasText(customProductEntity.getName())) {
				customProductEntity.setName(inventoryItemEntity.getName());
			}
			if (!StringUtils.hasText(customProductEntity.getDescription())) {
				customProductEntity.setDescription(inventoryItemEntity.getDescription());
			}
			customProductEntity.setCategory(inventoryItemEntity.getVariant().getProductBase().getCategory());
			var savedCustomProductEntity = customProductRepository.save(customProductEntity);

			var savedCustomProductTag = saveCustomProductTag(savedCustomProductEntity.getId(), customProductRequest.getTags());
			if (savedCustomProductTag.isEmpty()) {
				var inventoryItemTags = inventoryItemEntity.getTags().stream()
					.map(inventoryItemTagEntity -> new CustomProductTagEntity(savedCustomProductEntity.getId(), inventoryItemTagEntity.getName()))
					.collect(Collectors.toSet());
				savedCustomProductEntity.setTags(new HashSet<>(customProductTagRepository.saveAll(inventoryItemTags)));
			} else {
				savedCustomProductEntity.setTags(new HashSet<>(savedCustomProductTag));
			}
			return savedCustomProductEntity;
		}).collect(Collectors.toSet());

		var createCampaignHistoryEntity = campaignHistoryRepository.save(CampaignHistoryEntity.builder()
			.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
			.action(CampaignHistoryAction.CREATE.getByteValue())
			.build()
		);

		campaignEntity.setCustomProducts(saveCustomProducts);
		campaignEntity.setCampaignHistories(Set.of(createCampaignHistoryEntity));
		return campaignMapper.entityToDetailResponse(campaignEntity);
	}

	@Override
	@Transactional
	public CampaignDetailResponse updateCampaign(Long ownerId, CampaignRequest request) {
		var oldCampaignEntity = campaignRepository.findById(request.getId())
			.orElseThrow(() -> new EntityNotFoundException("campaign " + request.getId() + " not valid"));

		if (!oldCampaignEntity.getOwner().getId().equals(ownerId)) {
			throw new IllegalArgumentException("You not own campaign " + request.getId());
		}

		if (!oldCampaignEntity.getStatus().equals(CampaignStatus.DRAFT.getByteValue())) {
			throw new IllegalArgumentException("You can only update campaign with status DRAFT");
		}

		validateCreateCustomProductRequest(ownerId, request.getProviderId(), request.getCustomProducts());

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

		oldCampaignEntity.setStatus(CampaignStatus.DRAFT.getByteValue());
		oldCampaignEntity.setName(request.getName());
		oldCampaignEntity.setDescription(request.getDescription());
		oldCampaignEntity.getCustomProducts().clear();
		oldCampaignEntity.getCustomProducts().addAll(saveCustomProducts);
		var savedCampaignEntity = campaignRepository.save(oldCampaignEntity);
		return campaignMapper.entityToDetailResponse(savedCampaignEntity);
	}

	private List<CustomProductTagEntity> saveCustomProductTag(Long customProductId, Set<String> tags) {
		if (tags == null || tags.isEmpty()) {
			return Collections.emptyList();
		}
		return customProductTagRepository.saveAll(
			tags.stream()
				.map(tag -> new CustomProductTagEntity(customProductId, tag))
				.collect(Collectors.toSet())
		);
	}

	private void validateCreateCustomProductRequest(Long ownerId,
													String providerId,
													Set<CustomProductRequest> requests) {
		if (!providerRepository.existsById(providerId)) {
			throw new IllegalArgumentException("providerId " + providerId + " not valid");
		}

		for (var customProductRequest : requests) {
			var inventoryItemEntity = inventoryItemRepository.findById(customProductRequest.getInventoryItemId())
				.orElseThrow(() -> new IllegalArgumentException("inventoryItem " + customProductRequest.getInventoryItemId() + " not valid"));

			if (!ownerId.equals(inventoryItemEntity.getArtist().getId())) {
				throw new IllegalArgumentException("you not own inventoryItem " + customProductRequest.getInventoryItemId());
			}

			var providerConfig = inventoryItemEntity.getVariant().getProviderConfigs().stream()
				.filter(config -> config.getId().getBusinessCode().equals(providerId))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("inventoryItem " + customProductRequest.getInventoryItemId() + " is not supported by provider " + providerId));

			if (customProductRequest.getQuantity() != null
				&& providerConfig.getMinQuantity() > customProductRequest.getQuantity()) {
				throw new IllegalArgumentException("customProduct quantity must greater than " + providerConfig.getMinQuantity());
			}

			if (customProductRequest.getPrice() != null
				&& customProductRequest.getPrice().getAmount() != null
				&& providerConfig.getBasePriceAmount().compareTo(customProductRequest.getPrice().getAmount()) > 0) {
				throw new IllegalArgumentException("customProduct price amount must greater than " + providerConfig.getBasePriceAmount());
			}
		}
	}

	@Override
	public Page<CampaignResponse> getAllCampaigns(Specification<CampaignEntity> specification, Pageable pageable) {
		return campaignRepository.findAll(specification, pageable).map(campaignMapper::entityToResponse);
	}

	@Override
	public CampaignDetailResponse getCampaignDetail(Long userId, Long campaignId) {
		var userEntity = accountRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("user " + userId + " not found"));

		var campaignEntity = campaignRepository.findById(campaignId)
			.orElseThrow(() -> new EntityNotFoundException("campaignId " + campaignId + " not valid"));

		if (userEntity.getRole() == Role.ARTIST.getByteValue() && !campaignEntity.getOwner().getId().equals(userId)) {
			throw new IllegalArgumentException("You not own campaign " + campaignId);
		}

		return campaignMapper.entityToDetailResponse(campaignEntity);
	}

	@Override
	@Transactional
	public CampaignDetailResponse artistUpdateStatus(Long artistId, UpdateCampaignStatusRequest request) {
		var campaignEntity = campaignRepository.findById(request.getId())
			.orElseThrow(() -> new EntityNotFoundException("campaignId " + request.getId() + " not valid"));

		if (!campaignEntity.getOwner().getId().equals(artistId)) {
			throw new IllegalArgumentException("You not own campaign " + request.getId());
		}

		return switch (request.getStatus()) {
			case CANCELED -> artistCancelCampaign(campaignEntity, request.getMessage());
			case WAITING -> artistSubmitCampaign(campaignEntity, request.getMessage());
			default -> throw new IllegalArgumentException("You can only update campaign to status WAITING or CANCELED");
		};
	}

	private CampaignDetailResponse artistCancelCampaign(CampaignEntity campaignEntity, String message) {
		if (campaignEntity.getStatus() != CampaignStatus.DRAFT.getByteValue() &&
			campaignEntity.getStatus() != CampaignStatus.WAITING.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from DRAFT or WAITING to CANCELED");
		}

		if (campaignEntity.getStatus() == CampaignStatus.WAITING.getByteValue()) {
			campaignEntity.getCustomProducts().stream()
				.map(CustomProductEntity::getInventoryItem)
				.forEach(inventoryItemEntity -> {
					inventoryItemEntity.setCampaignLock(null);
					inventoryItemRepository.save(inventoryItemEntity);
				});
		}

		campaignEntity.setStatus(CampaignStatus.CANCELED.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.CANCEL.getByteValue())
				.message(message)
				.build()
		);

		return campaignMapper.entityToDetailResponse(campaignRepository.save(campaignEntity));
	}

	private CampaignDetailResponse artistSubmitCampaign(CampaignEntity campaignEntity, String message) {
		if (campaignEntity.getStatus() != CampaignStatus.DRAFT.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from DRAFT to WAITING");
		}

		for (var customProductEntity : campaignEntity.getCustomProducts()) {
			if (!StringUtils.hasText(customProductEntity.getName())) {
				throw new IllegalArgumentException("customProduct " + customProductEntity.getId() + " name must not empty");
			}
			if (customProductEntity.getQuantity() != null) {
				throw new IllegalArgumentException("customProduct " + customProductEntity.getId() + " quantity must not null");
			}
			if (customProductEntity.getPriceUnit() != null) {
				throw new IllegalArgumentException("customProduct " + customProductEntity.getId() + " price.Unit must not null");
			}
			if (customProductEntity.getPriceAmount() != null) {
				throw new IllegalArgumentException("customProduct " + customProductEntity.getId() + " price.Amount must not null");
			}
		}

		campaignEntity.getCustomProducts().stream()
			.map(CustomProductEntity::getInventoryItem)
			.forEach(inventoryItemEntity -> {
				if (inventoryItemEntity.getCampaignLock() != null) {
					throw new IllegalArgumentException("inventoryItem " + inventoryItemEntity.getId() + " is locked by another campaign: " + inventoryItemEntity.getCampaignLock());
				}
				inventoryItemEntity.setCampaignLock(campaignEntity.getId());
				inventoryItemRepository.save(inventoryItemEntity);
			});

		campaignEntity.setStatus(CampaignStatus.WAITING.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.SUMMIT.getByteValue())
				.message(message)
				.build()
		);

		return campaignMapper.entityToDetailResponse(campaignRepository.save(campaignEntity));
	}


	@Override
	@Transactional
	public CampaignDetailResponse reviewCampaign(Long staffId, UpdateCampaignStatusRequest request) {
		var campaignEntity = campaignRepository.findById(request.getId())
			.orElseThrow(() -> new EntityNotFoundException("campaignId " + request.getId() + " not valid"));

		return switch (request.getStatus()) {
			case DRAFT -> staffRequestChangeCampaign(campaignEntity, request.getMessage());
			case APPROVED -> staffApproveCampaign(campaignEntity, request.getMessage());
			case REJECTED -> staffRejectCampaign(campaignEntity, request.getMessage());
			default -> throw new IllegalArgumentException("You can only update campaign to status WAITING or CANCELED");
		};
	}

	private CampaignDetailResponse staffRequestChangeCampaign(CampaignEntity campaignEntity, String message) {
		if (campaignEntity.getStatus() != CampaignStatus.WAITING.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from WAITING to REQUEST_CHANGE");
		}

		campaignEntity.getCustomProducts().stream()
			.map(CustomProductEntity::getInventoryItem)
			.forEach(inventoryItemEntity -> {
				inventoryItemEntity.setCampaignLock(null);
				inventoryItemRepository.save(inventoryItemEntity);
			});

		campaignEntity.setStatus(CampaignStatus.DRAFT.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.REQUEST_CHANGE.getByteValue())
				.message(message)
				.build()
		);

		return campaignMapper.entityToDetailResponse(campaignRepository.save(campaignEntity));
	}

	private CampaignDetailResponse staffApproveCampaign(CampaignEntity campaignEntity, String message) {
		if (campaignEntity.getStatus() != CampaignStatus.WAITING.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from WAITING to APPROVED");
		}

		campaignEntity.getCustomProducts().stream()
			.map(CustomProductEntity::getInventoryItem)
			.forEach(inventoryItemEntity -> {
				inventoryItemEntity.setCampaignLock(null);
				inventoryItemRepository.save(inventoryItemEntity);
			});

		campaignEntity.setStatus(CampaignStatus.APPROVED.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.APPROVE.getByteValue())
				.message(message)
				.build()
		);

		return campaignMapper.entityToDetailResponse(campaignRepository.save(campaignEntity));
	}

	private CampaignDetailResponse staffRejectCampaign(CampaignEntity campaignEntity, String message) {
		if (campaignEntity.getStatus() != CampaignStatus.WAITING.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from WAITING to REJECTED");
		}

		campaignEntity.getCustomProducts().stream()
			.map(CustomProductEntity::getInventoryItem)
			.forEach(inventoryItemEntity -> {
				inventoryItemEntity.setCampaignLock(null);
				inventoryItemRepository.save(inventoryItemEntity);
			});

		campaignEntity.setStatus(CampaignStatus.REJECTED.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.REJECT.getByteValue())
				.message(message)
				.build()
		);

		return campaignMapper.entityToDetailResponse(campaignRepository.save(campaignEntity));
	}

}
