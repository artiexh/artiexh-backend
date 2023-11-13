package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductInventoryEntity;
import com.artiexh.data.jpa.projection.ProductInventoryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventoryEntity, String> {

	@Modifying
	@Query("update ProductInventoryEntity productInventory " +
		"set productInventory.quantity = productInventory.quantity + :quantity " +
		"where productInventory.productCode = :productCode")
    void updateQuantity(@Param("productCode") String productCode, @Param("quantity") Long quantity);

	@Query(
		nativeQuery = true,
		value = """
select pi.product_code as productCode, pi.product_in_campaign_id as productInCampaignId
from product_inventory pi
join product_in_campaign pic on pi.product_in_campaign_id = pic.id
where pic.campaign_id = :campaignId
"""
	)
	Set<ProductInventoryCode> getAllByCampaignId(@Param("campaignId") Long campaignId);
}
