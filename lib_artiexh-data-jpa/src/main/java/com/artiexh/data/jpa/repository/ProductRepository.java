package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductEntityId;
import com.artiexh.data.jpa.projection.ProductInSaleId;
import com.artiexh.data.jpa.projection.SoldProduct;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static org.hibernate.jpa.HibernateHints.*;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, ProductEntityId>, JpaSpecificationExecutor<ProductEntity> {

	Set<ProductEntity> findByIdIn(Collection<ProductEntityId> id);

	@Query(
		nativeQuery = true,
		value = "select product_code as productCode, campaign_sale_id as campaignSaleId from product where campaign_sale_id = :id"
	)
	Set<ProductInSaleId> findAllByCampaignSaleId(@Param("id") Long campaignSaleId);

	Page<ProductEntity> findProductEntitiesByCampaignSaleId(Long campaignSaleId, Pageable pageable);

	@QueryHints(value = {
		@QueryHint(name = HINT_FETCH_SIZE, value = "1"),
		@QueryHint(name = HINT_CACHEABLE, value = "false"),
		@QueryHint(name = HINT_READ_ONLY, value = "true")
	})
	@Query("select p from ProductEntity p")
	Stream<ProductEntity> streamAll();

	@Query(nativeQuery = true,
	value = """
  select p.product_code as productCode, od.quantity as quantity, p.sold_quantity as soldQuantity, od.price_amount as priceAmount 
  from product p 
  left outer join order_detail od on p.product_code = od.product_code and p.campaign_sale_id = od.campaign_sale_id 
  where p.campaign_sale_id = :campaignSaleId""")
	Set<SoldProduct> getSoldProducts(@Param("campaignSaleId") String campaignSaleId);

}
