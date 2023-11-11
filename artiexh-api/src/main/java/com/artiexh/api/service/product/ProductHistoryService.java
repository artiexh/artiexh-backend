package com.artiexh.api.service.product;

import com.artiexh.data.jpa.entity.ProductHistoryEntity;
import com.artiexh.model.domain.ProductHistory;
import com.artiexh.model.domain.ProductInventoryQuantity;
import com.artiexh.model.domain.ProductHistoryAction;
import com.artiexh.model.domain.SourceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public interface ProductHistoryService {

	void create(ProductHistoryAction action, Long sourceId, SourceCategory sourceCategory, Set<ProductInventoryQuantity> productQuantities);

	Page<ProductHistory> getInPage(Pageable pageable, Specification<ProductHistoryEntity> specification);

	ProductHistory getById(Long id);
}
