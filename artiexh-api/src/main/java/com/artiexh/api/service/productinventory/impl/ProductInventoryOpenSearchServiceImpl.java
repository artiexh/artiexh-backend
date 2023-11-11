package com.artiexh.api.service.productinventory.impl;

import com.artiexh.api.service.productinventory.ProductInventoryOpenSearchService;
import com.artiexh.data.opensearch.model.ProductInventoryDocument;
import com.artiexh.model.domain.ProductInventory;
import com.artiexh.model.mapper.ProductInventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInventoryOpenSearchServiceImpl implements ProductInventoryOpenSearchService {
	private final ElasticsearchOperations openSearchTemplate;
	private final ProductInventoryMapper productInventoryMapper;

	@Override
	public ProductInventoryDocument create(ProductInventory productInventory) {
		return openSearchTemplate.save(productInventoryMapper.domainToDocument(productInventory));
	}

	@Override
	public ProductInventoryDocument update(ProductInventory productInventory) {
		return openSearchTemplate.save(productInventoryMapper.domainToDocument(productInventory));
	}

}
