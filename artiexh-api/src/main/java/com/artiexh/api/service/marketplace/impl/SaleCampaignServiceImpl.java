package com.artiexh.api.service.marketplace.impl;

import com.artiexh.api.service.marketplace.ProductService;
import com.artiexh.api.service.marketplace.SaleCampaignService;
import com.artiexh.data.jpa.entity.CampaignSaleEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductEntityId;
import com.artiexh.data.jpa.repository.ArtistRepository;
import com.artiexh.data.jpa.repository.CampaignSaleRepository;
import com.artiexh.data.jpa.repository.ProductInventoryRepository;
import com.artiexh.model.mapper.CampaignSaleMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.marketplace.filter.SaleCampaignFilter;
import com.artiexh.model.rest.marketplace.request.ProductInSaleRequest;
import com.artiexh.model.rest.marketplace.request.SaleCampaignRequest;
import com.artiexh.model.rest.marketplace.response.SaleCampaignDetailResponse;
import com.artiexh.model.rest.marketplace.response.SaleCampaignResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
			.orderFrom(request.getOrderFrom())
			.orderTo(request.getOrderTo())
			.createdBy(creatorId)
			.build());

		var result = campaignSaleMapper.entityToDetailDomain(entity);

		var products = request.getProducts().stream()
			.map(productRequest -> productService.create(ProductEntity.builder()
				.id(new ProductEntityId(productRequest.getProductCode(), entity.getId()))
				.priceAmount(productRequest.getPrice().getAmount())
				.priceUnit(productRequest.getPrice().getUnit())
				.build()
			))
			.map(productMapper::domainToProductInSaleResponse)
			.collect(Collectors.toSet());
		result.setProducts(products);

		return result;
	}

	private void validateProductsRequest(Set<ProductInSaleRequest> productRequests) {
		var productCodes = productRequests.stream().map(ProductInSaleRequest::getProductCode).collect(Collectors.toSet());
		var entities = productInventoryRepository.findAllById(productCodes);
		if (entities.size() != productCodes.size()) {
			throw new IllegalArgumentException("Some products are not found");
		}
		for (var productEntity : entities) {
			for (var productRequest : productRequests) {
				if (productEntity.getProductCode().equals(productRequest.getProductCode())
					&& (productEntity.getQuantity() < productRequest.getQuantity())) {
					throw new IllegalArgumentException("Product " + productEntity.getProductCode() + " has not enough quantity");
				}
			}
		}
	}

	@Override
	public Page<SaleCampaignResponse> getAll(Pageable pageable, Specification<CampaignSaleEntity> specification) {
		return campaignSaleRepository.findAll(specification, pageable)
			.map(campaignSaleMapper::entityToDomain);
	}

	@Override
	public SaleCampaignDetailResponse getDetail(Long id) {
		return campaignSaleRepository.findById(id)
			.map(campaignSaleMapper::entityToDetailDomain)
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
}
