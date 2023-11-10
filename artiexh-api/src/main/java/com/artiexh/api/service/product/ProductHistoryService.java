package com.artiexh.api.service.product;

import com.artiexh.model.domain.ProductInventoryQuantity;
import com.artiexh.model.domain.ProductHistoryAction;
import com.artiexh.model.domain.SourceCategory;

import java.util.Set;

public interface ProductHistoryService {

	void create(ProductHistoryAction action, Long sourceId, SourceCategory sourceCategory, Set<ProductInventoryQuantity> productQuantities);
}
