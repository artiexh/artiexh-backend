package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.InventoryItemTagEntity;
import com.artiexh.data.jpa.entity.InventoryItemTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemTagRepository extends JpaRepository<InventoryItemTagEntity, InventoryItemTagId> {
}
