package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.InventoryItemEntity;
import com.artiexh.model.domain.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface InventoryService {
	InventoryItem save(InventoryItem item);

	Page<InventoryItem> getAll(Specification<InventoryItemEntity> specification, Pageable pageable);

	InventoryItem getById(Long userId, Long id);
}
