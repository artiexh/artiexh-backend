package com.artiexh.api.service.product.impl;

import com.artiexh.api.service.product.ProductHistoryService;
import com.artiexh.data.jpa.entity.ProductHistoryDetailEntity;
import com.artiexh.data.jpa.entity.ProductHistoryEntity;
import com.artiexh.data.jpa.entity.embededmodel.ProductHistoryEntityDetailId;
import com.artiexh.model.domain.ProductInventoryQuantity;
import com.artiexh.data.jpa.repository.ProductHistoryRepository;
import com.artiexh.model.domain.ProductHistoryAction;
import com.artiexh.model.domain.SourceCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductHistoryServiceImpl implements ProductHistoryService {
	private final ProductHistoryRepository productHistoryRepository;
	@Override
	@Transactional
	public void create(ProductHistoryAction action, Long sourceId, SourceCategory sourceCategory, Set<ProductInventoryQuantity> productQuantities) {
		Set<ProductHistoryDetailEntity> productHistoryDetails = new HashSet<>();
		ProductHistoryEntity productHistory = ProductHistoryEntity.builder()
			.action(action.getByteValue())
			.sourceId(sourceId)
			.sourceCategory(sourceCategory == null ? null : sourceCategory.getByteValue())
			.productHistoryDetails(productHistoryDetails)
			.build();
		productHistoryRepository.save(productHistory);
		productHistory.getProductHistoryDetails().addAll(productQuantities.stream()
			.map(product -> ProductHistoryDetailEntity.builder()
				.quantity(product.getQuantity())
				.id(ProductHistoryEntityDetailId.builder()
					.productHistoryId(productHistory.getId())
					.productCode(product.getProductCode())
					.build())
				.quantity(product.getQuantity())
				.build())
			.collect(Collectors.toSet()));
	}
}
