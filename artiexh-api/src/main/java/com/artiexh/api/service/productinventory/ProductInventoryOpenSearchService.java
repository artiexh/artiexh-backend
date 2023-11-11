package com.artiexh.api.service.productinventory;

import com.artiexh.data.opensearch.model.ProductInventoryDocument;
import com.artiexh.model.domain.ProductInventory;

public interface ProductInventoryOpenSearchService {

	ProductInventoryDocument create(ProductInventory productInventory);

	ProductInventoryDocument update(ProductInventory productInventory);

}
