package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventoryEntity, String> {

	@Modifying
	@Query("update ProductInventoryEntity productInventory " +
		"set productInventory.quantity = productInventory.quantity + :quantity " +
		"where productInventory.productCode = :productCode")
    void updateQuantity(@Param("productCode") String productCode, @Param("quantity") Long quantity);
}
