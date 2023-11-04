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
import com.artiexh.model.rest.campaign.request.*;
import com.artiexh.model.rest.campaign.response.*;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
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
	private final CampaignHistoryRepository campaignHistoryRepository;
	private final AccountRepository accountRepository;
	private final ProductInCampaignMapper productInCampaignMapper;
	private final CampaignMapper campaignMapper;
	private final ProductService productService;
	private final ProductMapper productMapper;
	private final ProviderMapper providerMapper;
	private final ArtistRepository artistRepository;

	@Override
	@Transactional
	public CampaignDetailResponse createCampaign(Long ownerId, ArtistCampaignRequest request) {
		ArtistEntity ownerEntity = artistRepository.getReferenceById(ownerId);
		validateCampaignTypeWithRole(ownerEntity, request);
		validateCreateCustomProductRequest(ownerEntity, request.getProviderId(), request.getProducts());

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
				.from(request.getFrom())
				.to(request.getTo())
				.build()
		);

		var savedProductInCampaigns = request.getProducts().stream().map(productInCampaignRequest -> {
			var customProductEntity = customProductRepository.getReferenceById(productInCampaignRequest.getCustomProductId());

			var productInCampaignEntity = productInCampaignMapper.createRequestToEntity(productInCampaignRequest);
			productInCampaignEntity.setCustomProduct(customProductEntity);
			productInCampaignEntity.setCampaign(campaignEntity);

			return productInCampaignRepository.save(productInCampaignEntity);
		}).collect(Collectors.toSet());

		var createCampaignHistoryEntity = campaignHistoryRepository.save(CampaignHistoryEntity.builder()
			.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
			.action(CampaignHistoryAction.CREATE.getByteValue())
			.updatedBy(ownerEntity.getUsername())
			.build()
		);

		campaignEntity.setProductInCampaigns(savedProductInCampaigns);
		campaignEntity.setCampaignHistories(Set.of(createCampaignHistoryEntity));
		return buildCampaignDetailResponse(campaignEntity);
	}

	@Override
	@Transactional
	public CampaignDetailResponse createPublicCampaign(Long createdBy, CreatePublicCampaignRequest request) {
		ArtistEntity ownerEntity = artistRepository.getReferenceById(request.getArtistId());
		AccountEntity account = accountRepository.getReferenceById(createdBy);

		CampaignEntity campaignEntity = CampaignEntity.builder()
			.owner(ownerEntity)
			.status(CampaignStatus.DRAFT.getByteValue())
			.name(request.getName())
			.type(CampaignType.PUBLIC.getByteValue())
			.createdBy(account)
			.build();
		campaignRepository.save(campaignEntity);

		CampaignHistoryEntity campaignHistory = CampaignHistoryEntity.builder()
			.id(CampaignHistoryId.builder().campaignId(campaignEntity.getId()).build())
			.action(CampaignHistoryAction.CREATE.getByteValue())
			.updatedBy(account.getUsername())
			.build();
//		campaignHistoryRepository.save(campaignHistory);

		campaignEntity.setCampaignHistories(Set.of(campaignHistory));
		return buildCampaignDetailResponse(campaignEntity);
	}

	@Override
	@Transactional
	public CampaignDetailResponse updateCampaign(Long ownerId, ArtistCampaignRequest request) {
		ArtistEntity ownerEntity = artistRepository.getReferenceById(ownerId);
		validateCampaignTypeWithRole(ownerEntity, request);

		var oldCampaignEntity = campaignRepository.findById(request.getId())
			.orElseThrow(() -> new EntityNotFoundException("campaign " + request.getId() + " not valid"));

		if (!oldCampaignEntity.getOwner().getId().equals(ownerId)) {
			throw new IllegalArgumentException("You not own campaign " + request.getId());
		}

		if (!oldCampaignEntity.getStatus().equals(CampaignStatus.DRAFT.getByteValue())
			&& !oldCampaignEntity.getStatus().equals(CampaignStatus.REQUEST_CHANGE.getByteValue())) {
			throw new IllegalArgumentException("You can only update campaign with status DRAFT or REQUEST_CHANGE");
		}

		validateCreateCustomProductRequest(ownerEntity, request.getProviderId(), request.getProducts());

		var customProductIdToProductInCampaignMap = oldCampaignEntity.getProductInCampaigns().stream()
			.collect(Collectors.toMap(
				productInCampaignEntity -> productInCampaignEntity.getCustomProduct().getId(),
				productInCampaign -> productInCampaign)
			);

		var productInCampaigns = request.getProducts().stream()
			.map(productInCampaignRequest -> {
				ProductInCampaignEntity productInCampaignEntity =
					customProductIdToProductInCampaignMap.get(productInCampaignRequest.getCustomProductId());

				if (productInCampaignEntity != null) {
					productInCampaignMapper.createRequestToEntity(productInCampaignRequest, productInCampaignEntity);
				} else {
					var customProductEntity = customProductRepository.getReferenceById(productInCampaignRequest.getCustomProductId());
					productInCampaignEntity = productInCampaignMapper.createRequestToEntity(productInCampaignRequest);
					productInCampaignEntity.setCustomProduct(customProductEntity);
					productInCampaignEntity.setCampaign(oldCampaignEntity);
				}

				return productInCampaignRepository.save(productInCampaignEntity);
			})
			.collect(Collectors.toSet());

		oldCampaignEntity.setName(request.getName());
		oldCampaignEntity.setProviderId(request.getProviderId());
		oldCampaignEntity.setDescription(request.getDescription());
		oldCampaignEntity.setThumbnailUrl(request.getThumbnailUrl());
		oldCampaignEntity.setContent(request.getContent());
		oldCampaignEntity.setType(request.getType().getByteValue());
		oldCampaignEntity.setFrom(request.getFrom());
		oldCampaignEntity.setTo(request.getTo());
		oldCampaignEntity.getProductInCampaigns().clear();
		oldCampaignEntity.getProductInCampaigns().addAll(productInCampaigns);

		var savedCampaignEntity = campaignRepository.save(oldCampaignEntity);
		return buildCampaignDetailResponse(savedCampaignEntity);
	}

	private CampaignDetailResponse buildCampaignDetailResponse(CampaignEntity campaignEntity) {
		var result = campaignMapper.entityToDetailResponse(campaignEntity);
		if (campaignEntity.getProviderId() != null) {
			var provider = providerRepository.getReferenceById(campaignEntity.getProviderId());
			result.setProvider(providerMapper.entityToInfo(provider));
			fillProviderConfigToResponse(campaignEntity.getProviderId(), result.getProducts());
		}
		return result;
	}

	private void fillProviderConfigToResponse(String providerId, Set<ProductInCampaignResponse> productInCampaignResponses) {
		for (var productInCampaignResponse : productInCampaignResponses) {
			customProductRepository.getReferenceById(productInCampaignResponse.getCustomProduct().getId())
				.getVariant().getProviderConfigs().stream()
				.filter(config -> config.getId().getBusinessCode().equals(providerId))
				.findAny()
				.map(providerConfigEntity -> new ProviderConfigResponse(
					providerConfigEntity.getManufacturingTime(),
					providerConfigEntity.getMinQuantity(),
					providerConfigEntity.getBasePriceAmount()
				))
				.ifPresent(productInCampaignResponse::setProviderConfig);
		}
	}

	private void validateCampaignTypeWithRole(ArtistEntity ownerEntity, ArtistCampaignRequest request) {
		Role role = Role.fromValue(ownerEntity.getRole());
		CampaignType type = request.getType();

		if (role == Role.ARTIST && type == CampaignType.PUBLIC) {
			throw new IllegalArgumentException("Artist cannot create public campaign");
		}

		if (role == Role.ADMIN && type != CampaignType.PUBLIC) {
			throw new IllegalArgumentException("Admin can only create public campaign");
		}
	}

	private void validateCreateCustomProductRequest(ArtistEntity ownerEntity,
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
				throw new IllegalArgumentException("You not own customProduct " + customProductEntity.getId());
			}

			validateProviderSupportCustomProduct(providerId, customProductEntity, productInCampaignRequest);
		}
	}

	private void validateProviderSupportCustomProduct(String providerId,
													  CustomProductEntity customProductEntity,
													  ProductInCampaignRequest productRequest) {
		if (providerId != null) {
			var providerConfig = customProductEntity.getVariant().getProviderConfigs().stream()
				.filter(config -> providerId.equals(config.getId().getBusinessCode()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("customProduct " + customProductEntity.getId() + " is not supported by provider " + providerId));

			if (productRequest.getQuantity() != null && providerConfig.getMinQuantity() > productRequest.getQuantity()) {
				throw new IllegalArgumentException("product quantity must greater than " + providerConfig.getMinQuantity());
			}

			if (productRequest.getPrice() != null
				&& productRequest.getPrice().getAmount() != null
				&& providerConfig.getBasePriceAmount().compareTo(productRequest.getPrice().getAmount()) > 0) {
				throw new IllegalArgumentException("product price amount must greater than " + providerConfig.getBasePriceAmount());
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

		if (userEntity.getRole() == Role.ADMIN.getByteValue()) {
			if (!ALLOWED_ADMIN_VIEW_STATUS.contains(CampaignStatus.fromValue(campaignEntity.getStatus()))
				&& !campaignEntity.getOwner().getId().equals(userId)) {
				throw new IllegalArgumentException("You can only get campaigns after submitted");
			}
		} else if (!campaignEntity.getOwner().getId().equals(userId)) {
			throw new IllegalArgumentException("You not own campaign " + campaignId);
		}

		return buildCampaignDetailResponse(campaignEntity);
	}

	@Override
	public PublishedCampaignDetailResponse getCampaignDetail(Long campaignId) {
		CampaignEntity campaignEntity = campaignRepository.findById(campaignId)
			.orElseThrow(EntityNotFoundException::new);

		if (!Boolean.TRUE.equals(campaignEntity.getIsPublished())) {
			throw new IllegalArgumentException(ErrorCode.CAMPAIGN_UNPUBLISHED.getMessage());
		}

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

		if (campaignEntity.getFrom() == null || campaignEntity.getTo() == null) {
			throw new IllegalArgumentException("From, to must not be null");
		}

		if (campaignEntity.getProductInCampaigns().isEmpty()) {
			throw new IllegalArgumentException("Campaign products must not be empty");
		}

		for (var productInCampaignEntity : campaignEntity.getProductInCampaigns()) {
			if (productInCampaignEntity.getQuantity() == null) {
				throw new IllegalArgumentException("customProduct " + productInCampaignEntity.getId() + " quantity must not null");
			}
			if (productInCampaignEntity.getPriceUnit() == null) {
				throw new IllegalArgumentException("customProduct " + productInCampaignEntity.getId() + " price.Unit must not null");
			}
			if (productInCampaignEntity.getPriceAmount() == null) {
				throw new IllegalArgumentException("customProduct " + productInCampaignEntity.getId() + " price.Amount must not null");
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

		if (Boolean.TRUE.equals(campaign.getIsPublished())) {
			throw new IllegalArgumentException("Product in this campaign is published");
		}

		Set<Long> productCampaignIds = campaign.getProductInCampaigns().stream()
			.map(ProductInCampaignEntity::getId)
			.collect(Collectors.toSet());
		Set<Long> productIds = products.stream()
			.map(PublishProductRequest::getProductInCampaignId)
			.collect(Collectors.toSet());

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
