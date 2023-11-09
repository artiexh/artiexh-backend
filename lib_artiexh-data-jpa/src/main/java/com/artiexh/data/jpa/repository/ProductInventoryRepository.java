package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventoryEntity, String> {
}
