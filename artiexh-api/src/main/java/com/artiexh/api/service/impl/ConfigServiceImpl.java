package com.artiexh.api.service.impl;

import com.artiexh.api.service.ConfigService;
import com.artiexh.api.service.product.OpenSearchProductService;
import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductInCampaignEntity;
import com.artiexh.data.jpa.repository.ProductInCampaignRepository;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductInCampaign;
import com.artiexh.model.mapper.ProductMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
	private final ProductRepository productRepository;
	private final OpenSearchProductService openSearchProductService;
	private final ProductMapper productMapper;
	private final ProductInCampaignRepository productInCampaignRepository;

	@Override
	public void syncProductToOpenSearch() {
		productRepository.streamAllByAvailableStatus()
			.map(productMapper::entityToDocument)
			.forEach(openSearchProductService::save);
	}

	@Override
	@Transactional
	public void createCampaignProduct(ProductInCampaign productInCampaign) {
		ProductInCampaignEntity campaignProduct = ProductInCampaignEntity.builder()
			.campaign(CampaignEntity.builder()
				.id(productInCampaign.getCampaign().getId())
				.build())
			.customProduct(CustomProductEntity.builder()
				.id(productInCampaign.getCustomProduct().getId())
				.build())
			.priceAmount(productInCampaign.getPrice().getAmount())
			.priceUnit(productInCampaign.getPrice().getUnit())
			.quantity(productInCampaign.getQuantity())
			.weight(productInCampaign.getWeight())
			.build();
		productInCampaignRepository.save(campaignProduct);
	}

	@Override
	public void syncProductToOpenSearch(Long productId) {
		ProductEntity productEntity = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);
		Product product = productMapper.entityToDomain(productEntity);
		openSearchProductService.save(product);
	}

}
