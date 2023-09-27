package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductBaseRepository extends JpaRepository<ProductBaseEntity, Long>,
	JpaSpecificationExecutor<ProductBaseEntity> {
	@Modifying(clearAutomatically = true)
	@Query("update ProductBaseEntity product " +
		"set product.hasVariant = true " +
		"where product.id = :productBaseId and product.hasVariant = false")
	void updateVariant(@Param("productBaseId") Long productBaseId);
}
