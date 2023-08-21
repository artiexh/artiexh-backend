package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ShopProductEntity;
import com.artiexh.data.jpa.entity.ShopProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopProductRepository extends JpaRepository<ShopProductEntity, ShopProductId> {
	@Query(value = "SELECT shopProduct FROM ShopProductEntity shopProduct WHERE shopProduct.shop.isDefault = true AND shopProduct.id.productId = :productId")
	ShopProductEntity findDefaultProductByProductId(@Param("productId") Long productId);
}
