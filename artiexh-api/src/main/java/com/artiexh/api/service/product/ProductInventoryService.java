package com.artiexh.api.service.product;

import com.artiexh.data.jpa.entity.ProductInCampaignEntity;
import com.artiexh.data.jpa.entity.ProductInventoryEntity;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductInventory;
import com.artiexh.model.rest.product.response.ProductResponse;
import com.artiexh.model.rest.producttemplate.ProductTemplateInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductInventoryService {
	Page<ProductInventory> getInPage(Specification<ProductInventoryEntity> specification, Pageable pageable);
	ProductInventory getDetail(String productCode);
	ProductInventory update(ProductInventory product);

	ProductInventory create(Long ownerId, ProductInventory product, ProductInCampaignEntity productInCampaign);
}
