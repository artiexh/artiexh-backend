package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItemEntity, Long>, JpaSpecificationExecutor<InventoryItemEntity> {
}
