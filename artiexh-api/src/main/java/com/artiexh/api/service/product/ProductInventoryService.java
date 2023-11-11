package com.artiexh.api.service.product;

import com.artiexh.data.jpa.entity.ProductInCampaignEntity;
import com.artiexh.data.jpa.entity.ProductInventoryEntity;
import com.artiexh.model.domain.ProductInventory;
import com.artiexh.model.domain.ProductInventoryQuantity;
import com.artiexh.model.rest.product.request.UpdateProductQuantitiesRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public interface ProductInventoryService {
	Page<ProductInventory> getInPage(Specification<ProductInventoryEntity> specification, Pageable pageable);
	ProductInventory getDetail(String productCode);
	ProductInventory update(ProductInventory product);
	void updateQuantities(UpdateProductQuantitiesRequest productQuantity);
	ProductInventory create(Long ownerId, ProductInventory product, ProductInCampaignEntity productInCampaign);

	void updateQuantityFromCampaignRequest(Set<Long> productInCampaignIds, Long sourceId, Set<ProductInventoryQuantity> productQuantities);
}
