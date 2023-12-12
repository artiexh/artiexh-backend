package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductEntityId;
import com.artiexh.data.jpa.projection.ProductInSaleId;
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

//	@Modifying
//	@Query("update ProductEntity entity set entity.status = cast(1 as byte) where entity.id = :id")
//	void softDelete(long id);
//
//	Set<ProductEntity> findAllByIdInAndShopIdAndStatus(Set<Long> ids, Long shopId, byte status);
//
//	Set<ProductEntity> findAllByIdIn(Set<Long> ids);

//	int countAllByIdIn(Collection<Long> id);

	Page<ProductEntity> findProductEntitiesByCampaignSaleId(Long campaignSaleId, Pageable pageable);

	@QueryHints(value = {
		@QueryHint(name = HINT_FETCH_SIZE, value = "1"),
		@QueryHint(name = HINT_CACHEABLE, value = "false"),
		@QueryHint(name = HINT_READ_ONLY, value = "true")
	})
	@Query("select p from ProductEntity p")
	Stream<ProductEntity> streamAllByAvailableStatus();
}
