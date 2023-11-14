package com.artiexh.api.service.marketplace.impl;

import com.artiexh.api.service.marketplace.ProductService;
import com.artiexh.api.service.marketplace.SaleCampaignService;
import com.artiexh.api.service.productinventory.ProductInventoryJpaService;
import com.artiexh.data.jpa.entity.CampaignSaleEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductEntityId;
import com.artiexh.data.jpa.repository.ArtistRepository;
import com.artiexh.data.jpa.repository.CampaignSaleRepository;
import com.artiexh.data.jpa.repository.ProductInventoryRepository;
import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductInventoryQuantity;
import com.artiexh.model.domain.SourceCategory;
import com.artiexh.model.mapper.CampaignSaleMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.marketplace.salecampaign.filter.SaleCampaignFilter;
import com.artiexh.model.rest.marketplace.salecampaign.request.ProductInSaleRequest;
import com.artiexh.model.rest.marketplace.salecampaign.request.SaleCampaignRequest;
import com.artiexh.model.rest.marketplace.salecampaign.response.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SaleCampaignServiceImpl implements SaleCampaignService {
	private final CampaignSaleRepository campaignSaleRepository;
	private final ProductInventoryRepository productInventoryRepository;
	private final ArtistRepository artistRepository;
	private final CampaignSaleMapper campaignSaleMapper;
	private final ProductMapper productMapper;
	private final ProductService productService;
	private final ProductInventoryJpaService productInventoryJpaService;

	@Override
	@Transactional
	public SaleCampaignDetailResponse createSaleCampaign(long creatorId, SaleCampaignRequest request) {
		if (request.getPublicDate().isAfter(request.getOrderFrom())) {
			throw new IllegalArgumentException("Public date must be before or equal to order from date");
		}

		if (request.getOrderFrom().isAfter(request.getOrderTo())) {
			throw new IllegalArgumentException("Order from date must be before order to date");
		}

		validateProductsRequest(request.getProducts());

		var entity = campaignSaleRepository.save(CampaignSaleEntity.builder()
			.name(request.getName())
			.description(request.getDescription())
			.publicDate(request.getPublicDate())
			.from(request.getOrderFrom())
			.to(request.getOrderTo())
			.createdBy(creatorId)
			.build());

		var result = campaignSaleMapper.entityToDetailResponse(entity);

		Set<ProductInventoryQuantity> productQuantities = new HashSet<>();
		Set<ProductInSaleCampaignResponse> productResponses = new HashSet<>();

		for (var productRequest : request.getProducts()) {
			Product product = productService.create(ProductEntity.builder()
				.id(new ProductEntityId(productRequest.getProductCode(), entity.getId()))
				.priceAmount(productRequest.getPrice().getAmount())
				.priceUnit(productRequest.getPrice().getUnit())
				.build()
			);
			productQuantities.add(new ProductInventoryQuantity(
				product.getProductInventory().getProductCode(),
				(long) productRequest.getQuantity())
			);
			productResponses.add(productMapper.domainToProductInSaleResponse(product));
		}

		productInventoryJpaService.reduceQuantity(result.getId(), SourceCategory.CAMPAIGN_SALE, productQuantities);

		return result;
	}

	private void validateProductsRequest(Set<ProductInSaleRequest> productRequests) {
		var productCodes = productRequests.stream().map(ProductInSaleRequest::getProductCode).collect(Collectors.toSet());
		var entities = productInventoryRepository.findAllById(productCodes);
		if (entities.size() != productCodes.size()) {
			throw new IllegalArgumentException("Some products are not found");
		}
		for (var productInventoryEntity : entities) {
			for (var productRequest : productRequests) {
				if (productInventoryEntity.getProductCode().equals(productRequest.getProductCode())
					&& (productInventoryEntity.getQuantity() < productRequest.getQuantity())) {
					throw new IllegalArgumentException("Product " + productInventoryEntity.getProductCode() + " has not enough quantity");
				}
			}
		}
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
	public Page<SaleCampaignResponse> getAllByArtist(String artistUsername,
													 Pageable pageable,
													 SaleCampaignFilter filter) {
		if (!artistRepository.existsByUsername(artistUsername)) {
			throw new EntityNotFoundException("Artist not found");
		}
		filter.setUsername(artistUsername);
		return getAll(pageable, filter.getMarketplaceSpecification());
	}

	@Override
	public CampaignStatistics getStatistics(Long campaignId) {
		CampaignSaleEntity campaignEntity = campaignSaleRepository.findById(campaignId).orElseThrow(EntityNotFoundException::new);
		Set<ProductEntity> products = campaignEntity.getProducts().stream().sorted(Comparator.comparingInt(ProductEntity::getSoldQuantity)).collect(Collectors.toCollection(LinkedHashSet::new));

		BigDecimal revenue = BigDecimal.ZERO;
		BigDecimal profit = BigDecimal.ZERO;
		Set<ProductStatisticResponse> productStatisticResponses = new HashSet<>();

		for (ProductEntity product : products) {
			revenue = revenue.add(product.getPriceAmount().multiply(BigDecimal.valueOf(product.getQuantity())));
			profit = profit.add(product.getArtistProfit().multiply(BigDecimal.valueOf(product.getSoldQuantity())));

			productStatisticResponses.add(
				ProductStatisticResponse.builder()
					.name(product.getProductInventory().getName())
					.productCode(product.getProductInventory().getProductCode())
					.soldQuantity(Long.valueOf(product.getSoldQuantity()))
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
}
