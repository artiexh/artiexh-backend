package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
	@Query(value = "SELECT shop FROM ShopEntity shop WHERE shop.isDefault = true AND shop.ownerShop.id = :artistId")
	ShopEntity findDefaultByArtistId(@Param("artistId") Long artistId);
}
