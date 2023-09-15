package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvidedProductBaseRepository extends JpaRepository<ProductVariantEntity, Long>,
	JpaSpecificationExecutor<ProductVariantEntity> {

	Optional<ProductVariantEntity> findByProvidedProductBaseId(ProvidedProductBaseId providedProductBaseId);
}
