package com.artiexh.api.service.campaign.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.api.service.product.ProductService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.*;
import com.artiexh.model.mapper.CampaignMapper;
import com.artiexh.model.mapper.ProductInCampaignMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.mapper.ProviderMapper;
import com.artiexh.model.rest.campaign.request.CampaignRequest;
import com.artiexh.model.rest.campaign.request.ProductInCampaignRequest;
import com.artiexh.model.rest.campaign.request.PublishProductRequest;
import com.artiexh.model.rest.campaign.request.UpdateCampaignStatusRequest;
import com.artiexh.model.rest.campaign.response.*;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.artiexh.model.domain.CampaignStatus.ALLOWED_ADMIN_VIEW_STATUS;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
	private final CampaignRepository campaignRepository;
	private final ProductInCampaignRepository productInCampaignRepository;
	private final CustomProductRepository customProductRepository;
	private final ProviderRepository providerRepository;
	private final ProductInCampaignTagRepository productInCampaignTagRepository;
	private final CampaignHistoryRepository campaignHistoryRepository;
	private final AccountRepository accountRepository;
	private final ProductInCampaignMapper productInCampaignMapper;
	private final CampaignMapper campaignMapper;
	private final ProductService productService;
	private final ProductMapper productMapper;
	private final ProviderMapper providerMapper;
	private final ArtistRepository artistRepository;
	private final ProductRepository productRepository;

	@Override
	@Transactional
	public CampaignDetailResponse createCampaign(Long ownerId, CampaignRequest request) {
		ArtistEntity ownerEntity = artistRepository.getReferenceById(ownerId);
		if (ownerEntity.getRole() == Role.ARTIST.getByteValue()
			&& request.getType() == CampaignType.PUBLIC) {
			throw new IllegalArgumentException("Artist cannot create public campaign");
		}

		if (ownerEntity.getRole() == Role.ADMIN.getByteValue()
			&& request.getType() != CampaignType.PUBLIC) {
			throw new IllegalArgumentException("Admin can only create public campaign");
		}

		validateCreateCustomProductRequest(ownerEntity, request.getProviderId(), request.getProductInCampaigns());

		var campaignEntity = campaignRepository.save(
			CampaignEntity.builder()
				.status(CampaignStatus.DRAFT.getByteValue())
				.owner(ownerEntity)
				.providerId(request.getProviderId())
				.name(request.getName())
				.description(request.getDescription())
				.thumbnailUrl(request.getThumbnailUrl())
				.content(request.getContent())
				.type(request.getType().getByteValue())
				.build()
		);

		Map<Long, ProviderConfigResponse> providerConfigsByProductInCampaignId = new HashMap<>();
		var saveProductInCampaigns = request.getProductInCampaigns().stream().map(productInCampaignRequest -> {
			var customProductEntity = customProductRepository.getReferenceById(productInCampaignRequest.getCustomProductId());

			var productInCampaignEntity = productInCampaignMapper.createRequestToEntity(productInCampaignRequest);
			productInCampaignEntity.setCustomProduct(customProductEntity);
			productInCampaignEntity.setCampaign(campaignEntity);
			if (!StringUtils.hasText(productInCampaignEntity.getName())) {
				productInCampaignEntity.setName(customProductEntity.getName());
			}
			if (!StringUtils.hasText(productInCampaignEntity.getDescription())) {
				productInCampaignEntity.setDescription(customProductEntity.getDescription());
			}
			productInCampaignEntity.setCategory(customProductEntity.getVariant().getProductTemplate().getCategory());
			var savedProductInCampaignEntity = productInCampaignRepository.save(productInCampaignEntity);

			var savedProductInCampaignTag = saveProductInCampaignTag(savedProductInCampaignEntity.getId(), productInCampaignRequest.getTags());
			if (savedProductInCampaignTag.isEmpty()) {
				var inventoryItemTags = customProductEntity.getTags().stream()
					.map(inventoryItemTagEntity -> new ProductInCampaignTagEntity(savedProductInCampaignEntity.getId(), inventoryItemTagEntity.getName()))
					.collect(Collectors.toSet());
				savedProductInCampaignEntity.setTags(new HashSet<>(productInCampaignTagRepository.saveAll(inventoryItemTags)));
			} else {
				savedProductInCampaignEntity.setTags(new HashSet<>(savedProductInCampaignTag));
			}

			customProductEntity.getVariant().getProviderConfigs().stream()
				.filter(config -> config.getId().getBusinessCode().equals(request.getProviderId()))
				.findAny()
				.ifPresent(providerConfigEntity ->
					providerConfigsByProductInCampaignId.put(
						savedProductInCampaignEntity.getId(),
						new ProviderConfigResponse(
							providerConfigEntity.getManufacturingTime(),
							providerConfigEntity.getMinQuantity(),
							providerConfigEntity.getBasePriceAmount()
						)));
			return savedProductInCampaignEntity;
		}).collect(Collectors.toSet());

		var createCampaignHistoryEntity = campaignHistoryRepository.save(CampaignHistoryEntity.builder()
			.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
			.action(CampaignHistoryAction.CREATE.getByteValue())
			.updatedBy(ownerEntity.getUsername())
			.build()
		);

		campaignEntity.setProductInCampaigns(saveProductInCampaigns);
		campaignEntity.setCampaignHistories(Set.of(createCampaignHistoryEntity));
		var result = campaignMapper.entityToDetailResponse(campaignEntity);

		if (request.getProviderId() != null) {
			var provider = providerRepository.getReferenceById(request.getProviderId());
			result.setProvider(providerMapper.entityToInfo(provider));
		}

		for (var productInCampaignResponse : result.getProductInCampaigns()) {
			productInCampaignResponse.setProviderConfig(providerConfigsByProductInCampaignId.get(productInCampaignResponse.getId()));
		}
		return result;
	}

	@Override
	@Transactional
	public CampaignDetailResponse updateCampaign(Long ownerId, CampaignRequest request) {
		ArtistEntity ownerEntity = artistRepository.getReferenceById(ownerId);
		if (ownerEntity.getRole() == Role.ARTIST.getByteValue()
			&& request.getType() == CampaignType.PUBLIC) {
			throw new IllegalArgumentException("Artist cannot create public campaign");
		}

		if (ownerEntity.getRole() == Role.ADMIN.getByteValue()
			&& request.getType() != CampaignType.PUBLIC) {
			throw new IllegalArgumentException("Admin can only create public campaign");
		}

		var oldCampaignEntity = campaignRepository.findById(request.getId())
			.orElseThrow(() -> new EntityNotFoundException("campaign " + request.getId() + " not valid"));

		if (!oldCampaignEntity.getOwner().getId().equals(ownerId)) {
			throw new IllegalArgumentException("You not own campaign " + request.getId());
		}

		if (!oldCampaignEntity.getStatus().equals(CampaignStatus.DRAFT.getByteValue())
			&& !oldCampaignEntity.getStatus().equals(CampaignStatus.REQUEST_CHANGE.getByteValue())) {
			throw new IllegalArgumentException("You can only update campaign with status DRAFT or REQUEST_CHANGE");
		}

		validateCreateCustomProductRequest(ownerEntity, request.getProviderId(), request.getProductInCampaigns());

		var customProductIdToProductInCampaignMap = oldCampaignEntity.getProductInCampaigns().stream()
			.collect(Collectors.toMap(
				productInCampaignEntity -> productInCampaignEntity.getCustomProduct().getId(),
				productInCampaign -> productInCampaign)
			);

		Map<Long, ProviderConfigResponse> providerConfigsByProductInCampaignId = new HashMap<>();
		var productInCampaigns = request.getProductInCampaigns().stream()
			.map(productInCampaignRequest -> {
				ProductInCampaignEntity productInCampaignEntity = customProductIdToProductInCampaignMap.get(productInCampaignRequest.getCustomProductId());
				if (productInCampaignEntity != null) {
					productInCampaignMapper.createRequestToEntity(productInCampaignRequest, productInCampaignEntity);
				} else {
					var customProductEntity = customProductRepository.getReferenceById(productInCampaignRequest.getCustomProductId());

					productInCampaignEntity = productInCampaignMapper.createRequestToEntity(productInCampaignRequest);
					productInCampaignEntity.setCustomProduct(customProductEntity);
					productInCampaignEntity.setCampaign(oldCampaignEntity);
				}

				productInCampaignEntity.getTags().clear();
				var savedProductInCampaignEntity = productInCampaignRepository.save(productInCampaignEntity);

				var savedProductInCampaignTag = saveProductInCampaignTag(savedProductInCampaignEntity.getId(), productInCampaignRequest.getTags());
				savedProductInCampaignEntity.getTags().addAll(savedProductInCampaignTag);

				savedProductInCampaignEntity.getCustomProduct().getVariant().getProviderConfigs().stream()
					.filter(config -> config.getId().getBusinessCode().equals(request.getProviderId()))
					.findAny()
					.ifPresent(providerConfigEntity ->
						providerConfigsByProductInCampaignId.put(
							savedProductInCampaignEntity.getId(),
							new ProviderConfigResponse(
								providerConfigEntity.getManufacturingTime(),
								providerConfigEntity.getMinQuantity(),
								providerConfigEntity.getBasePriceAmount()
							)));
				return savedProductInCampaignEntity;
			})
			.collect(Collectors.toSet());

		oldCampaignEntity.setName(request.getName());
		oldCampaignEntity.setDescription(request.getDescription());
		oldCampaignEntity.setThumbnailUrl(request.getThumbnailUrl());
		oldCampaignEntity.setContent(request.getContent());
		oldCampaignEntity.getProductInCampaigns().clear();
		oldCampaignEntity.getProductInCampaigns().addAll(productInCampaigns);
		var savedCampaignEntity = campaignRepository.save(oldCampaignEntity);

		var result = campaignMapper.entityToDetailResponse(savedCampaignEntity);

		if (request.getProviderId() != null) {
			var provider = providerRepository.getReferenceById(request.getProviderId());
			result.setProvider(providerMapper.entityToInfo(provider));
		}
		for (var productInCampaignResponse : result.getProductInCampaigns()) {
			productInCampaignResponse.setProviderConfig(providerConfigsByProductInCampaignId.get(productInCampaignResponse.getId()));
		}
		return result;
	}

	private List<ProductInCampaignTagEntity> saveProductInCampaignTag(Long productInCampaignId, Set<String> tags) {
		if (tags == null || tags.isEmpty()) {
			return Collections.emptyList();
		}
		return productInCampaignTagRepository.saveAll(
			tags.stream()
				.map(tag -> new ProductInCampaignTagEntity(productInCampaignId, tag))
				.collect(Collectors.toSet())
		);
	}

	private void validateCreateCustomProductRequest(AccountEntity ownerEntity,
													String providerId,
													Set<ProductInCampaignRequest> requests) {
		if (providerId != null && !providerRepository.existsById(providerId)) {
			throw new IllegalArgumentException("providerId " + providerId + " not valid");
		}

		for (var productInCampaignRequest : requests) {
			var customProductEntity = customProductRepository.findById(productInCampaignRequest.getCustomProductId())
				.orElseThrow(() -> new IllegalArgumentException("customProduct " + productInCampaignRequest.getCustomProductId() + " not valid"));

			if (ownerEntity.getRole() == Role.ARTIST.getByteValue()
				&& !ownerEntity.getId().equals(customProductEntity.getArtist().getId())) {
				throw new IllegalArgumentException("you not own customProduct " + productInCampaignRequest.getCustomProductId());
			}

			if (providerId != null) {
				var providerConfig = customProductEntity.getVariant().getProviderConfigs().stream()
					.filter(config -> config.getId().getBusinessCode().equals(providerId))
					.findAny()
					.orElseThrow(() -> new IllegalArgumentException("customProduct " + productInCampaignRequest.getCustomProductId() + " is not supported by provider " + providerId));

				if (productInCampaignRequest.getQuantity() != null
					&& providerConfig.getMinQuantity() > productInCampaignRequest.getQuantity()) {
					throw new IllegalArgumentException("customProduct quantity must greater than " + providerConfig.getMinQuantity());
				}

				if (productInCampaignRequest.getPrice() != null
					&& productInCampaignRequest.getPrice().getAmount() != null
					&& providerConfig.getBasePriceAmount().compareTo(productInCampaignRequest.getPrice().getAmount()) > 0) {
					throw new IllegalArgumentException("customProduct price amount must greater than " + providerConfig.getBasePriceAmount());
				}
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

		if ((userEntity.getRole() == Role.ADMIN.getByteValue() || userEntity.getRole() == Role.STAFF.getByteValue())
			&& !ALLOWED_ADMIN_VIEW_STATUS.contains(CampaignStatus.fromValue(campaignEntity.getStatus()))) {
			throw new IllegalArgumentException("You can only get campaigns after submitted");
		}

		Map<Long, ProviderConfigResponse> providerConfigsByProductInCampaignId = new HashMap<>();
		for (var productInCampaignEntity : campaignEntity.getProductInCampaigns()) {
			productInCampaignEntity.getCustomProduct().getVariant().getProviderConfigs().stream()
				.filter(config -> config.getId().getBusinessCode().equals(campaignEntity.getProviderId()))
				.findAny()
				.ifPresent(providerConfigEntity ->
					providerConfigsByProductInCampaignId.put(
						productInCampaignEntity.getId(),
						new ProviderConfigResponse(
							providerConfigEntity.getManufacturingTime(),
							providerConfigEntity.getMinQuantity(),
							providerConfigEntity.getBasePriceAmount()
						)));
		}

		var result = campaignMapper.entityToDetailResponse(campaignEntity);

		if (campaignEntity.getProviderId() != null) {
			var provider = providerRepository.getReferenceById(campaignEntity.getProviderId());
			result.setProvider(providerMapper.entityToInfo(provider));
		}
		for (var productInCampaignResponse : result.getProductInCampaigns()) {
			productInCampaignResponse.setProviderConfig(providerConfigsByProductInCampaignId.get(productInCampaignResponse.getId()));
		}
		return result;
	}

	@Override
	public PublishedCampaignDetailResponse getCampaignDetail(Long campaignId) {
		CampaignEntity campaignEntity = campaignRepository.findById(campaignId)
			.orElseThrow(EntityNotFoundException::new);
		PublishedCampaignDetailResponse result = campaignMapper.entityToPublishedCampaignDetailResponse(campaignEntity);

		if (campaignEntity.getProviderId() != null) {
			ProviderEntity provider = providerRepository.findById(campaignEntity.getProviderId()).orElse(ProviderEntity.builder().build());
			result.setProvider(providerMapper.entityToInfo(provider));
		} else {
			throw new IllegalArgumentException(ErrorCode.PROVIDER_NOT_FOUND.getMessage());
		}
		return result;
	}

	@Override
	@Transactional
	public CampaignResponse artistUpdateStatus(Long artistId, UpdateCampaignStatusRequest request) {
		var campaignEntity = campaignRepository.findById(request.getId())
			.orElseThrow(() -> new EntityNotFoundException("campaignId " + request.getId() + " not valid"));

		if (!campaignEntity.getOwner().getId().equals(artistId)) {
			throw new IllegalArgumentException("You not own campaign " + request.getId());
		}

		ArtistEntity artistEntity = artistRepository.getReferenceById(artistId);

		return switch (request.getStatus()) {
			case CANCELED -> artistCancelCampaign(campaignEntity, artistEntity, request.getMessage());
			case WAITING -> artistSubmitCampaign(campaignEntity, artistEntity, request.getMessage());
			default -> throw new IllegalArgumentException("You can only update campaign to status WAITING or CANCELED");
		};
	}

	private CampaignResponse artistCancelCampaign(CampaignEntity campaignEntity, ArtistEntity artistEntity, String message) {
		if (campaignEntity.getStatus() != CampaignStatus.DRAFT.getByteValue()
			&& campaignEntity.getStatus() != CampaignStatus.REQUEST_CHANGE.getByteValue()
			&& campaignEntity.getStatus() != CampaignStatus.WAITING.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from DRAFT, REQUEST_CHANGE or WAITING to CANCELED");
		}

		if (campaignEntity.getStatus() == CampaignStatus.WAITING.getByteValue()) {
			campaignEntity.getProductInCampaigns().stream()
				.map(ProductInCampaignEntity::getCustomProduct)
				.forEach(customProductEntity -> {
					customProductEntity.setCampaignLock(null);
					customProductRepository.save(customProductEntity);
				});
		}

		campaignEntity.setStatus(CampaignStatus.CANCELED.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.CANCEL.getByteValue())
				.message(message)
				.updatedBy(artistEntity.getUsername())
				.build()
		);

		return campaignMapper.entityToResponse(campaignRepository.save(campaignEntity));
	}

	private CampaignResponse artistSubmitCampaign(CampaignEntity campaignEntity, ArtistEntity artistEntity, String message) {
		if (campaignEntity.getStatus() != CampaignStatus.DRAFT.getByteValue()
			&& campaignEntity.getStatus() != CampaignStatus.REQUEST_CHANGE.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from DRAFT, REQUEST_CHANGE to WAITING");
		}

		for (var productInCampginEntity : campaignEntity.getProductInCampaigns()) {
			if (!StringUtils.hasText(productInCampginEntity.getName())) {
				throw new IllegalArgumentException("customProduct " + productInCampginEntity.getId() + " name must not empty");
			}
			if (productInCampginEntity.getQuantity() == null) {
				throw new IllegalArgumentException("customProduct " + productInCampginEntity.getId() + " quantity must not null");
			}
			if (productInCampginEntity.getPriceUnit() == null) {
				throw new IllegalArgumentException("customProduct " + productInCampginEntity.getId() + " price.Unit must not null");
			}
			if (productInCampginEntity.getPriceAmount() == null) {
				throw new IllegalArgumentException("customProduct " + productInCampginEntity.getId() + " price.Amount must not null");
			}
		}

		campaignEntity.getProductInCampaigns().stream()
			.map(ProductInCampaignEntity::getCustomProduct)
			.forEach(customProductEntity -> {
				if (customProductEntity.getCampaignLock() != null) {
					throw new IllegalArgumentException("customProduct " + customProductEntity.getId() + " is locked by another campaign: " + customProductEntity.getCampaignLock());
				}
				customProductEntity.setCampaignLock(campaignEntity.getId());
				customProductRepository.save(customProductEntity);
			});

		campaignEntity.setStatus(CampaignStatus.WAITING.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.SUBMIT.getByteValue())
				.message(message)
				.updatedBy(artistEntity.getUsername())
				.build()
		);

		return campaignMapper.entityToResponse(campaignRepository.save(campaignEntity));
	}


	@Override
	@Transactional
	public CampaignResponse reviewCampaign(Long staffId, UpdateCampaignStatusRequest request) {
		var campaignEntity = campaignRepository.findById(request.getId())
			.orElseThrow(() -> new EntityNotFoundException("campaignId " + request.getId() + " not valid"));

		AccountEntity accountEntity = accountRepository.getReferenceById(staffId);
		return switch (request.getStatus()) {
			case REQUEST_CHANGE -> staffRequestChangeCampaign(campaignEntity, accountEntity, request.getMessage());
			case APPROVED -> staffApproveCampaign(campaignEntity, accountEntity, request.getMessage());
			case REJECTED -> staffRejectCampaign(campaignEntity, accountEntity, request.getMessage());
			case MANUFACTURING -> staffStartManufactureCampaign(campaignEntity, accountEntity, request.getMessage());
			case DONE -> staffFinishManufactureCampaign(campaignEntity, accountEntity, request.getMessage());
			default ->
				throw new IllegalArgumentException("You can only update campaign to status REQUEST_CHANGE, APPROVED or REJECTED");
		};
	}

	private CampaignResponse staffRequestChangeCampaign(CampaignEntity campaignEntity,
														AccountEntity accountEntity,
														String message) {
		if (campaignEntity.getStatus() != CampaignStatus.WAITING.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from WAITING to REQUEST_CHANGE");
		}

		campaignEntity.getProductInCampaigns().stream()
			.map(ProductInCampaignEntity::getCustomProduct)
			.forEach(inventoryItemEntity -> {
				inventoryItemEntity.setCampaignLock(null);
				customProductRepository.save(inventoryItemEntity);
			});

		campaignEntity.setStatus(CampaignStatus.REQUEST_CHANGE.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.REQUEST_CHANGE.getByteValue())
				.message(message)
				.updatedBy(accountEntity.getUsername())
				.build()
		);

		return campaignMapper.entityToResponse(campaignRepository.save(campaignEntity));
	}

	private CampaignResponse staffApproveCampaign(CampaignEntity campaignEntity,
												  AccountEntity accountEntity,
												  String message) {
		if (campaignEntity.getStatus() != CampaignStatus.WAITING.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from WAITING to APPROVED");
		}

		campaignEntity.getProductInCampaigns().stream()
			.map(ProductInCampaignEntity::getCustomProduct)
			.forEach(inventoryItemEntity -> {
				inventoryItemEntity.setCampaignLock(null);
				customProductRepository.save(inventoryItemEntity);
			});

		campaignEntity.setStatus(CampaignStatus.APPROVED.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.APPROVE.getByteValue())
				.message(message)
				.updatedBy(accountEntity.getUsername())
				.build()
		);

		return campaignMapper.entityToResponse(campaignRepository.save(campaignEntity));
	}

	private CampaignResponse staffRejectCampaign(CampaignEntity campaignEntity,
												 AccountEntity accountEntity,
												 String message) {
		if (campaignEntity.getStatus() != CampaignStatus.WAITING.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from WAITING to REJECTED");
		}

		campaignEntity.getProductInCampaigns().stream()
			.map(ProductInCampaignEntity::getCustomProduct)
			.forEach(inventoryItemEntity -> {
				inventoryItemEntity.setCampaignLock(null);
				customProductRepository.save(inventoryItemEntity);
			});

		campaignEntity.setStatus(CampaignStatus.REJECTED.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.REJECT.getByteValue())
				.message(message)
				.updatedBy(accountEntity.getUsername())
				.build()
		);

		return campaignMapper.entityToResponse(campaignRepository.save(campaignEntity));
	}

	private CampaignResponse staffStartManufactureCampaign(CampaignEntity campaignEntity,
														   AccountEntity accountEntity,
														   String message) {
		if (campaignEntity.getStatus() != CampaignStatus.APPROVED.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from APPROVED to MANUFACTURING");
		}

		campaignEntity.setStatus(CampaignStatus.MANUFACTURING.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.MANUFACTURING.getByteValue())
				.message(message)
				.updatedBy(accountEntity.getUsername())
				.build()
		);

		return campaignMapper.entityToResponse(campaignRepository.save(campaignEntity));
	}

	private CampaignResponse staffFinishManufactureCampaign(CampaignEntity campaignEntity,
															AccountEntity accountEntity,
															String message) {
		if (campaignEntity.getStatus() != CampaignStatus.APPROVED.getByteValue()
			&& campaignEntity.getStatus() != CampaignStatus.MANUFACTURING.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from APPROVED or MANUFACTURING to DONE");
		}

		campaignEntity.setStatus(CampaignStatus.DONE.getByteValue());
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.DONE.getByteValue())
				.message(message)
				.updatedBy(accountEntity.getUsername())
				.build()
		);

		return campaignMapper.entityToResponse(campaignRepository.save(campaignEntity));
	}

	@Override
	@Transactional
	public Set<ProductResponse> publishProduct(Long campaignId, Set<PublishProductRequest> products) {
		CampaignEntity campaign = campaignRepository.findById(campaignId).orElseThrow(EntityNotFoundException::new);

		if (!campaign.getStatus().equals(CampaignStatus.APPROVED.getByteValue())) {
			throw new IllegalArgumentException("You can only publish product after campaign's status is APPROVED");
		}

		if (campaign.getIsPublished()) {
			throw new IllegalArgumentException("Product in this campaign is published");
		}

		Set<Long> productCampaignIds = campaign.getProductInCampaigns().stream().map(ProductInCampaignEntity::getId).collect(Collectors.toSet());
		Set<Long> productIds = products.stream().map(PublishProductRequest::getProductInCampaignId).collect(Collectors.toSet());

		if (!productCampaignIds.equals(productIds)) {
			throw new IllegalArgumentException("All product in campaign must be published");
		}

		Set<ProductResponse> productResponses = new HashSet<>();
		for (PublishProductRequest unpublishedProduct : products) {
			Product product = productMapper.publishProductRequestToProduct(unpublishedProduct);

			product = productService.create(campaign.getOwner().getId(), product);
			productResponses.add(productMapper.domainToProductResponse(product));
		}

		staffPublishProductCampaign(campaign, "");

		return productResponses;
	}

	@Override
	public Page<ProductInCampaignResponse> getAllProductCampaign(Long campaignId, Pageable pageable) {
		return productInCampaignRepository.findAllByCampaignId(campaignId, pageable).map(productInCampaignMapper::entityToResponse);
	}

	private void staffPublishProductCampaign(CampaignEntity campaignEntity, String message) {
		if (campaignEntity.getStatus() != CampaignStatus.APPROVED.getByteValue()) {
			throw new IllegalArgumentException("You can only update campaign from APPROVED to PUBLISHED");
		}

		campaignEntity.setIsPublished(true);
		campaignEntity.getCampaignHistories().add(
			CampaignHistoryEntity.builder()
				.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
				.action(CampaignHistoryAction.PUBLISHED.getByteValue())
				.message(message)
				.build()
		);

		campaignRepository.save(campaignEntity);
	}

}
