package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductVariantEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, Long>,
	JpaSpecificationExecutor<ProductVariantEntity> {
}
