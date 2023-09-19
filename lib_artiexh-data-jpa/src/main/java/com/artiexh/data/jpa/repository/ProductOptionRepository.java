package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOptionEntity, Long>, JpaSpecificationExecutor<ProductOptionEntity> {
	Optional<ProductOptionEntity> findProductOptionEntityByProductIdAndId(Long productId, Long id);
}
