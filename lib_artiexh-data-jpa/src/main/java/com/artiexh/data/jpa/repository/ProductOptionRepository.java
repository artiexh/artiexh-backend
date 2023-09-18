package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOptionEntity, Long>, JpaSpecificationExecutor<ProductOptionEntity> {
}
