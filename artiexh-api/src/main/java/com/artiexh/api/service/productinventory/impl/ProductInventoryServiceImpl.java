package com.artiexh.api.service.productinventory.impl;

import com.artiexh.api.service.productinventory.ProductInventoryJpaService;
import com.artiexh.api.service.productinventory.ProductInventoryOpenSearchService;
import com.artiexh.api.service.productinventory.ProductInventoryService;
import com.artiexh.data.jpa.entity.ProductInCampaignEntity;
import com.artiexh.data.jpa.entity.ProductInventoryEntity;
import com.artiexh.model.domain.ProductInventory;
import com.artiexh.model.domain.ProductInventoryQuantity;
import com.artiexh.model.rest.product.request.UpdateProductQuantitiesRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductInventoryServiceImpl implements ProductInventoryService {
	private final ProductInventoryJpaService productInventoryJpaService;
	private final ProductInventoryOpenSearchService productInventoryOpenSearchService;

	@Override
	public Page<ProductInventory> getInPage(Specification<ProductInventoryEntity> specification, Pageable pageable) {
		return productInventoryJpaService.getInPage(specification, pageable);
	}

	@Override
	public ProductInventory getDetail(String productCode) {
		return productInventoryJpaService.getDetail(productCode);
	}

	@Override
	public ProductInventory update(ProductInventory product) {
		ProductInventory productInventory;
		try {
			productInventory = productInventoryJpaService.update(product);
			productInventoryOpenSearchService.update(productInventory);
		} catch (Exception ex) {
			log.warn("Update product inventory to db fail", ex);
			throw ex;
		}
		return productInventory;
	}

	@Override
	public void updateQuantities(UpdateProductQuantitiesRequest productQuantity) {
		productInventoryJpaService.updateQuantities(productQuantity);
	}

	@Override
	public ProductInventory create(Long ownerId, ProductInventory product, ProductInCampaignEntity productInCampaign) {
		ProductInventory productInventory;
		try {
			productInventory = productInventoryJpaService.create(ownerId, product, productInCampaign);
			productInventoryOpenSearchService.create(productInventory);
		} catch (Exception ex) {
			log.warn("Insert product inventory to db fail", ex);
			throw ex;
		}
		return productInventory;
	}

	@Override
	public void updateQuantityFromCampaignRequest(Set<Long> productInCampaignIds, Long sourceId, Set<ProductInventoryQuantity> productQuantities) {
		productInventoryJpaService.updateQuantityFromCampaignRequest(sourceId, productQuantities);
	}
}
