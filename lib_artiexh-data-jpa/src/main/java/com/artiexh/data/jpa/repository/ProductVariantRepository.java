package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.projection.ProductVariantCombinationQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, Long>,
	JpaSpecificationExecutor<ProductVariantEntity> {

	@Query(
		nativeQuery = true,
		value = """
select variant_id as variantId, option_value_id as optionValueId, count(option_id) as numOfCombination
from product_variant pv
    inner join product_variant_combination pvm on pv.id = pvm.variant_id
where product_base_id = :productBaseId
group by variant_id, product_base_id
order by count(option_id) desc;
"""
	)
	List<ProductVariantCombinationQuantity> finProductVariantCombinationQuantityByProductBaseId(@Param("productBaseId") Long productBaseId);
}
