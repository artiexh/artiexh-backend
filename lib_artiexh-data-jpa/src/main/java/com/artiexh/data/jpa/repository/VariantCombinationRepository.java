package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductVariantCombinationEntity;
import com.artiexh.data.jpa.entity.ProductVariantCombinationEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantCombinationRepository extends JpaRepository<ProductVariantCombinationEntity, ProductVariantCombinationEntityId> {
}
