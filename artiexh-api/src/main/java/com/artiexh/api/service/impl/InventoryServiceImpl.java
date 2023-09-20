package com.artiexh.api.service.impl;

import com.artiexh.api.service.InventoryService;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.ImageSetEntity;
import com.artiexh.data.jpa.entity.InventoryItemEntity;
import com.artiexh.data.jpa.repository.ImageSetRepository;
import com.artiexh.data.jpa.repository.InventoryRepository;
import com.artiexh.model.domain.ImageSet;
import com.artiexh.model.domain.InventoryItem;
import com.artiexh.model.mapper.CycleAvoidingMappingContext;
import com.artiexh.model.mapper.InventoryMapper;
import com.artiexh.model.mapper.MediaMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
	private final InventoryRepository inventoryRepository;
	private final ImageSetRepository imageSetRepository;
	private final InventoryMapper inventoryMapper;
	private final MediaMapper mediaMapper;
	@Override
	@Transactional
	public InventoryItem save(InventoryItem item) {
		InventoryItemEntity entity;
		if (item.getId() != null) {
			entity = updateItem(item);
		} else {
			entity = createItem(item);
		}

		return inventoryMapper.entityToDomain(entity, new CycleAvoidingMappingContext());
	}

	@Transactional
	public InventoryItemEntity createItem(InventoryItem item) {
		InventoryItemEntity entity = inventoryMapper.domainToEntity(item);

		//imageSet.setInventoryItem(entity);
		Set<ImageSetEntity> savedImageSet = new HashSet<>(entity.getImageSet());
		entity.setImageSet(savedImageSet);

		return inventoryRepository.save(entity);
	}

	@Transactional
	public InventoryItemEntity updateItem(InventoryItem item) {
		InventoryItemEntity entity = inventoryRepository.findById(item.getId()).orElseThrow(EntityNotFoundException::new);

		entity.getImageSet().clear();
		Set<ImageSetEntity> savedImageSet = new HashSet<>();
		for (ImageSet imageSet: item.getImageSet()) {
			ImageSetEntity imageSetEntity = mediaMapper.domainToEntity(imageSet);
			savedImageSet.add(imageSetEntity);
		}
		entity.getImageSet().addAll(savedImageSet);

		return inventoryRepository.save(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InventoryItem> getAll(Specification<InventoryItemEntity> specification, Pageable pageable) {
		Page<InventoryItemEntity> itemPage = inventoryRepository.findAll(specification, pageable);
		return itemPage.map(item -> inventoryMapper.entityToDomain(item, new CycleAvoidingMappingContext()));
	}
}
