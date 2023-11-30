package com.artiexh.api.service.marketplace.impl;

import com.artiexh.api.base.exception.ArtiexhConfigException;
import com.artiexh.api.base.service.SystemConfigService;
import com.artiexh.api.service.marketplace.ProductOpenSearchService;
import com.artiexh.api.service.marketplace.ProductService;
import com.artiexh.api.service.marketplace.SaleCampaignService;
import com.artiexh.api.service.productinventory.ProductInventoryJpaService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.projection.ProductInSaleId;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.data.opensearch.model.ProductDocument;
import com.artiexh.model.domain.*;
import com.artiexh.model.mapper.CampaignSaleMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.marketplace.salecampaign.filter.MarketplaceSaleCampaignFilter;
import com.artiexh.model.rest.marketplace.salecampaign.request.ProductInSaleRequest;
import com.artiexh.model.rest.marketplace.salecampaign.request.SaleCampaignRequest;
import com.artiexh.model.rest.marketplace.salecampaign.request.UpdateProductInSaleRequest;
import com.artiexh.model.rest.marketplace.salecampaign.response.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.artiexh.api.base.common.Const.SystemConfigKey.DEFAULT_PROFIT_PERCENTAGE;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SaleCampaignServiceImpl implements SaleCampaignService {
	private final CampaignSaleRepository campaignSaleRepository;
	private final ProductInventoryRepository productInventoryRepository;
	private final ArtistRepository artistRepository;
	private final CampaignRepository campaignRepository;
	private final ProductRepository productRepository;
	private final CampaignSaleMapper campaignSaleMapper;
	private final ProductMapper productMapper;
	private final ProductService productService;
	private final SystemConfigService systemConfigService;
	private final ProductOpenSearchService productOpenSearchService;
	private final ProductInventoryJpaService productInventoryJpaService;

	@Override
	@Transactional
	public SaleCampaignDetailResponse createSaleCampaign(long creatorId, SaleCampaignRequest request) {
		if (request.getPublicDate().isAfter(request.getFrom())) {
			throw new IllegalArgumentException("Public date must be before or equal to order from date");
		}

		if (request.getFrom().isAfter(request.getTo())) {
			throw new IllegalArgumentException("Order from date must be before order to date");
		}

		ArtistEntity artistEntity = artistRepository.findById(request.getArtistId())
			.orElseThrow(() -> new EntityNotFoundException("Artist " + request.getArtistId() + " not found"));


		var entity = campaignSaleRepository.save(CampaignSaleEntity.builder()
			.name(request.getName())
			.description(request.getDescription())
			.publicDate(request.getPublicDate())
			.from(request.getFrom())
			.to(request.getTo())
			.createdBy(creatorId)
			.owner(artistEntity)
			.content(request.getContent())
			.thumbnailUrl(request.getThumbnailUrl())
			.type(request.getType().getByteValue())
			.status(CampaignSaleStatus.DRAFT.getByteValue())
			.build());

		return campaignSaleMapper.entityToDetailResponse(entity);
	}

	@Override
	@Transactional
	public SaleCampaignDetailResponse createSaleCampaign(long creatorId, Long campaignRequestId) {
		int profitPercentageInt = Integer.parseInt(systemConfigService.getOrThrow(DEFAULT_PROFIT_PERCENTAGE, () -> new ArtiexhConfigException("Missing artiexh default profit percentage")));
		var profitPercentage = BigDecimal.valueOf(profitPercentageInt);

		CampaignEntity campaignEntity = campaignRepository.findById(campaignRequestId)
			.orElseThrow(() -> new EntityNotFoundException("Campaign request " + campaignRequestId + " not existed"));

		if (!Boolean.TRUE.equals(campaignEntity.getIsFinalized())) {
			throw new IllegalArgumentException("Campaign request must be finalized before create sale campaign");
		}

		if (campaignEntity.getCampaignSale() != null) {
			throw new IllegalArgumentException("Campaign request is in another sale campaign");
		}

		var entity = campaignSaleRepository.save(CampaignSaleEntity.builder()
			.name(campaignEntity.getName())
			.description(campaignEntity.getDescription())
			.from(campaignEntity.getFrom())
			.to(campaignEntity.getTo())
			.createdBy(creatorId)
			.owner(campaignEntity.getOwner())
			.content(campaignEntity.getContent())
			.thumbnailUrl(campaignEntity.getThumbnailUrl())
			.type(campaignEntity.getType())
			.status(CampaignSaleStatus.DRAFT.getByteValue())
			.campaignRequest(campaignEntity)
			.build());
		var result = campaignSaleMapper.entityToDetailResponse(entity);

		Set<Long> productInCampaignIds = campaignEntity.getProductInCampaigns().stream()
			.map(ProductInCampaignEntity::getId)
			.collect(Collectors.toSet());

		var productInventoryEntities = productInventoryRepository.findAllByProductInCampaignIdIn(productInCampaignIds);
		if (productInventoryEntities.size() != productInCampaignIds.size()) {
			throw new IllegalArgumentException("Finalize campaign request is not finished");
		}


		for (var productInventory : productInventoryEntities) {
			var profit = productInventory.getPriceAmount()
				.subtract(productInventory.getManufacturingPrice());
			var artistProfit = profit.subtract(
				profit.multiply(profitPercentage).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN)
			);
			productService.create(ProductEntity.builder()
				.id(new ProductEntityId(productInventory.getProductCode(), entity.getId()))
				.productInventory(productInventory)
				.campaignSale(entity)
				.priceAmount(productInventory.getPriceAmount())
				.priceUnit(productInventory.getPriceUnit())
				.quantity(productInventory.getQuantity().intValue())
				.artistProfit(artistProfit)
				.build());
		}
		return result;
	}

	@Override
	@Transactional
	public SaleCampaignDetailResponse updateSaleCampaign(Long id, SaleCampaignRequest request) {
		CampaignSaleEntity entity = campaignSaleRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Sale campaign " + id + " not found"));

		Instant now = Instant.now();

		if (now.isAfter(entity.getTo())) {
			throw new IllegalArgumentException("Cannot update sale campaign after it ends");
		}

		if (now.isAfter(entity.getFrom())) {
			if (request.getFrom() != entity.getFrom()) {
				throw new IllegalArgumentException("Cannot update sale campaign from after it starts");
			}
			if (request.getPublicDate() != entity.getPublicDate()) {
				throw new IllegalArgumentException("Cannot update sale campaign public date after it starts");
			}
		}

		if (request.getPublicDate().isAfter(request.getFrom())) {
			throw new IllegalArgumentException("Public date must be before or equal to order from date");
		} else {
			entity.setPublicDate(request.getPublicDate());
		}

		if (request.getFrom().isAfter(request.getTo())) {
			throw new IllegalArgumentException("Order from date must be before order to date");
		} else {
			entity.setFrom(request.getFrom());
			entity.setTo(request.getTo());
		}

		ArtistEntity artistEntity = artistRepository.findById(request.getArtistId())
			.orElseThrow(() -> new EntityNotFoundException("Artist " + request.getArtistId() + " not found"));

		if (entity.getOwner().getId() == null) {
			entity.setOwner(artistEntity);
		} else if (entity.getProducts().isEmpty()) {
			entity.setOwner(artistEntity);
		} else if (!entity.getOwner().getId().equals(artistEntity.getId())) {
			throw new IllegalArgumentException("Cannot change owner of sale campaign");
		}

		entity.setName(request.getName());
		entity.setDescription(request.getDescription());
		entity.setContent(request.getContent());
		entity.setThumbnailUrl(request.getThumbnailUrl());
		entity.setType(request.getType().getByteValue());

		//Update Campaign info to Opensearch
		ProductDocument.Campaign campaignInfo = campaignSaleMapper.entityToDocument(entity);
		Set<ProductInSaleId> productInSaleIds = productRepository.findAllByCampaignSaleId(entity.getId());
		Map<String, ProductDocument.Campaign> campaignMap = new HashMap<>();
		for (ProductInSaleId productInSaleId : productInSaleIds) {
			campaignMap.put(productInSaleId.getCampaignSaleId().toString() + "-" + productInSaleId.getProductCode(), campaignInfo);
		}
		productOpenSearchService.updateCampaignInfo(campaignMap);
		productService.refreshOpenSearchIndex();

		return campaignSaleMapper.entityToDetailResponse(entity);
	}

	@Override
	@Transactional
	public void updateStatus(Long id, CampaignSaleStatus status) {
		CampaignSaleEntity entity = campaignSaleRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Sale campaign " + id + " not found"));

		if (entity.getStatus() == status.getByteValue()) {
			return;
		}

		switch (CampaignSaleStatus.from(entity.getStatus())) {
			case DRAFT -> updateCampaignFromDraft(entity, status);
			case ACTIVE -> updateCampaignFromActive(entity, status);
			case CLOSED -> throw new IllegalArgumentException("Cannot update status of closed sale campaign");
		}
	}

	private void updateCampaignFromDraft(CampaignSaleEntity entity, CampaignSaleStatus status) {
		if (status == CampaignSaleStatus.CLOSED) {
			entity.setStatus(status.getByteValue());
			campaignSaleRepository.save(entity);
		} else { // ACTIVE
			entity.setStatus(status.getByteValue());
			campaignSaleRepository.save(entity);

			Set<ProductInventoryQuantity> productQuantities = entity.getProducts().stream()
				.map(productEntity -> {
					// check quantity
					if (productEntity.getQuantity() > productEntity.getProductInventory().getQuantity()) {
						throw new IllegalArgumentException("Product inventory have not enough quantity");
					}
					// update opensearch
					productOpenSearchService.updateCampaignStatus(
						entity.getId(),
						productEntity.getProductInventory().getProductCode(),
						status
					);
					return new ProductInventoryQuantity(
						productEntity.getProductInventory().getProductCode(),
						(long) productEntity.getQuantity() - productEntity.getSoldQuantity()
					);
				})
				.collect(Collectors.toSet());

			// reduce inventory quantity
			productInventoryJpaService.reduceQuantity(entity.getId(), SourceCategory.CAMPAIGN_SALE, productQuantities);
		}
	}

	private void updateCampaignFromActive(CampaignSaleEntity entity, CampaignSaleStatus status) {
		entity.setStatus(status.getByteValue());
		campaignSaleRepository.save(entity);

		Set<ProductInventoryQuantity> productQuantities = entity.getProducts().stream()
			.map(productEntity -> {
				// update opensearch
				productOpenSearchService.updateCampaignStatus(
					entity.getId(),
					productEntity.getProductInventory().getProductCode(),
					status
				);
				return new ProductInventoryQuantity(
					productEntity.getProductInventory().getProductCode(),
					(long) productEntity.getQuantity() - productEntity.getSoldQuantity()
				);
			})
			.collect(Collectors.toSet());

		// reduce inventory quantity
		productInventoryJpaService.refundQuantity(entity.getId(), SourceCategory.CAMPAIGN_SALE, productQuantities);
	}

	@Override
	public Page<SaleCampaignResponse> getAll(Pageable pageable, Specification<CampaignSaleEntity> specification) {
		return campaignSaleRepository.findAll(specification, pageable)
			.map(campaignSaleMapper::entityToResponse);
	}

	@Override
	public SaleCampaignDetailResponse getDetail(Long id) {
		return campaignSaleRepository.findById(id)
			.map(campaignSaleMapper::entityToDetailResponse)
			.orElseThrow(() -> new EntityNotFoundException("Sale campaign not found"));
	}

	@Override
	public SaleCampaignDetailResponse getDetail(Long id, Long ownerId) {
		return campaignSaleRepository.findCampaignSaleEntityByIdAndOwnerId(id, ownerId)
			.map(campaignSaleMapper::entityToDetailResponse)
			.orElseThrow(() -> new EntityNotFoundException("Sale campaign not found"));
	}

	@Override
	public Page<SaleCampaignResponse> getAllByArtist(String artistUsername,
													 Pageable pageable,
													 MarketplaceSaleCampaignFilter filter) {
		if (!artistRepository.existsByUsername(artistUsername)) {
			throw new EntityNotFoundException("Artist not found");
		}
		filter.setUsername(artistUsername);
		return getAll(pageable, filter.getMarketplaceSpecification());
	}

	@Override
	public CampaignStatistics getStatistics(Long campaignId) {
		CampaignSaleEntity campaignEntity = campaignSaleRepository.findById(campaignId).orElseThrow(EntityNotFoundException::new);
		List<ProductEntity> products = campaignEntity.getProducts().stream().sorted(Comparator.comparingInt(ProductEntity::getSoldQuantity).reversed()).toList();

		BigDecimal revenue = BigDecimal.ZERO;
		BigDecimal profit = BigDecimal.ZERO;
		List<ProductStatisticResponse> productStatisticResponses = new LinkedList<>();

		for (ProductEntity product : products) {
			revenue = revenue.add(product.getPriceAmount().multiply(BigDecimal.valueOf(product.getSoldQuantity())));
			profit = profit.add(product.getArtistProfit().multiply(BigDecimal.valueOf(product.getSoldQuantity())));

			productStatisticResponses.add(
				ProductStatisticResponse.builder()
					.name(product.getProductInventory().getName())
					.productCode(product.getProductInventory().getProductCode())
					.soldQuantity(Long.valueOf(product.getSoldQuantity()))
					.revenue(Money.builder()
						.amount(product.getPriceAmount().multiply(BigDecimal.valueOf(product.getSoldQuantity())))
						.unit("VND")
						.build())
					.quantity(Long.valueOf(product.getQuantity()))
					.build()
			);
		}

		ProductStatisticResponse bestSold = productStatisticResponses.stream().findFirst().orElse(null);
		ProductStatisticResponse worstSold = productStatisticResponses.stream().reduce((prev, next) -> next).orElse(null);

		return CampaignStatistics.builder()
			.revenue(Money.builder()
				.amount(revenue)
				.unit("VND")
				.build())
			.profit(Money.builder()
				.amount(profit)
				.unit("VND")
				.build())
			.campaignId(campaignId)
			.from(campaignEntity.getFrom())
			.to(campaignEntity.getTo())
			.name(campaignEntity.getName())
			.products(productStatisticResponses)
			.bestSoldProduct(bestSold)
			.worstSoldProduct(worstSold)
			.build();
	}

	@Override
	@Transactional
	public Set<ProductResponse> createProductInSaleCampaign(Long campaignId, Set<ProductInSaleRequest> requests) {
		CampaignSaleEntity entity = campaignSaleRepository.findById(campaignId)
			.orElseThrow(() -> new EntityNotFoundException("Sale campaign " + campaignId + " not found"));

		if (entity.getStatus() != CampaignSaleStatus.DRAFT.getByteValue()) {
			throw new IllegalStateException("Cannot add product to sale campaign that is not in draft status");
		}

		if (entity.getCampaignRequest() != null) {
			throw new IllegalArgumentException("Cannot add product to sale campaign created from campaign request");
		}

		if (entity.getOwner() == null) {
			throw new IllegalArgumentException("Sale campaign must have an owner before add product");
		}

		Stream<Product> result = requests.stream()
			.map(request -> {
				ProductInventoryEntity productInventory = productInventoryRepository.findById(request.getProductCode())
					.orElseThrow(() -> new IllegalArgumentException("Product " + request.getProductCode() + " not found"));
				if (!productInventory.getOwner().getId().equals(entity.getOwner().getId())) {
					throw new IllegalArgumentException("Product " + request.getProductCode() + " is not owned by sale campaign owner");
				}
				if (request.getQuantity() != null && request.getQuantity() > productInventory.getQuantity()) {
					throw new IllegalArgumentException("Product " + request.getProductCode() + " has not enough quantity");
				}
				return Pair.of(request, productInventory);
			})
			.map(pair -> {
					var productEntityBuilder = ProductEntity.builder()
						.id(new ProductEntityId(pair.getFirst().getProductCode(), entity.getId()))
						.productInventory(pair.getSecond())
						.campaignSale(entity)
						.artistProfit(pair.getFirst().getArtistProfit());
					if (pair.getFirst().getPrice() != null) {
						productEntityBuilder
							.priceAmount(pair.getFirst().getPrice().getAmount())
							.priceUnit(pair.getFirst().getPrice().getUnit());
					}
					if (pair.getFirst().getQuantity() != null) {
						productEntityBuilder.quantity(pair.getFirst().getQuantity());
					}

					return productService.create(productEntityBuilder.build());
				}
			);
		productService.refreshOpenSearchIndex();

		return result.map(productMapper::domainToProductResponse)
			.collect(Collectors.toSet());
	}

	@Override
	@Transactional
	public ProductResponse updateProductInSaleCampaign(long campaignId,
													   String productCode,
													   UpdateProductInSaleRequest request) {
		ProductEntity productEntity = productRepository.findById(new ProductEntityId(productCode, campaignId))
			.orElseThrow(() -> new EntityNotFoundException("Product " + productCode + " in campaign " + campaignId + " not found"));

		if (productEntity.getCampaignSale().getStatus() != CampaignSaleStatus.DRAFT.getByteValue()) {
			throw new IllegalStateException("Cannot add product to sale campaign that is not in draft status");
		}

		if (request.getQuantity() != null) {
			int compareResult = Integer.compare(request.getQuantity(), productEntity.getQuantity());
			if (compareResult > 0 && productEntity.getProductInventory().getQuantity() < request.getQuantity()) {
				// check inventory quantity
				throw new IllegalArgumentException("Product inventory have not enough quantity");
			}
			if (compareResult < 0 && request.getQuantity() < productEntity.getSoldQuantity()) {
				// check sold quantity
				throw new IllegalArgumentException("Product is sold more than request quantity");
			}
		}

		productEntity.setQuantity(request.getQuantity());
		if (request.getPrice() != null) {
			productEntity.setPriceAmount(request.getPrice().getAmount());
			productEntity.setPriceUnit(request.getPrice().getUnit());
		}
		productEntity.setArtistProfit(request.getArtistProfit());

		return productMapper.domainToProductResponse(productService.update(productEntity));
	}

	@Override
	@Transactional
	public void deleteProductInSaleCampaign(Long campaignId, Set<String> productCodes) {
		CampaignSaleEntity entity = campaignSaleRepository.findById(campaignId)
			.orElseThrow(() -> new EntityNotFoundException("Sale campaign " + campaignId + " not found"));

		if (entity.getStatus() != CampaignSaleStatus.DRAFT.getByteValue()) {
			throw new IllegalStateException("Cannot add product to sale campaign that is not in draft status");
		}

		if (entity.getCampaignRequest() != null) {
			throw new IllegalArgumentException("Cannot add product to sale campaign created from campaign request");
		}

		for (var productCode : productCodes) {
			var productEntity = productRepository.findById(new ProductEntityId(productCode, campaignId))
				.orElseThrow(() -> new IllegalArgumentException("Product " + productCode + " not in campaign"));
			productService.delete(productEntity);
		}
	}
}
