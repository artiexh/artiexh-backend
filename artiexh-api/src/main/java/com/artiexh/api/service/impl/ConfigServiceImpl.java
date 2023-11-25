package com.artiexh.api.service.impl;

import com.artiexh.api.service.ConfigService;
import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.ProductInCampaignEntity;
import com.artiexh.data.jpa.repository.CampaignSaleRepository;
import com.artiexh.data.jpa.repository.ProductInCampaignRepository;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.data.opensearch.model.ProductDocument;
import com.artiexh.model.domain.ProductInCampaign;
import com.artiexh.model.mapper.ProductInventoryMapper;
import com.artiexh.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	private final ProductInCampaignRepository productInCampaignRepository;
	private final CampaignSaleRepository campaignSaleRepository;
	private final ProductInventoryMapper productInventoryMapper;
	private final ElasticsearchOperations openSearchTemplate;

	@Override
	public void syncProductToOpenSearch() {
		IndexCoordinates coordinates = openSearchTemplate.getIndexCoordinatesFor(ProductDocument.class);
		Query query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
		String[] ids = openSearchTemplate.search(query, ProductDocument.class, coordinates)
			.stream()
			.map(SearchHit::getId)
			.toArray(String[]::new);
		Query idsQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.idsQuery().addIds(ids)).build();
		openSearchTemplate.delete(idsQuery, ProductDocument.class, coordinates);

		campaignSaleRepository.streamAllByFromBeforeAndToAfter(Instant.now())
			.forEach(campaign -> {
				for (var product : campaign.getProducts()) {
					var document = productMapper.entityToDocument(product);
					openSearchTemplate.save(document);
				}
			});
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

//	@Override
//	public void syncProductToOpenSearch(Long productId) {
//		ProductEntity productEntity = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);
//		Product product = productMapper.entityToDomain(productEntity);
//		openSearchProductService.save(product);
//	}

}
