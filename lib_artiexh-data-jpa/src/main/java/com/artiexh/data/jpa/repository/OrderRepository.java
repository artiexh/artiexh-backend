package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

	Optional<OrderEntity> findByIdAndShopId(Long orderId, Long artistId);

	@Modifying(flushAutomatically = true)
	@Query("update OrderEntity set status = cast(1 as byte) where orderGroupId = :id")
	void updatePayment(@Param("id") Long id);

	Set<OrderEntity> getAllByOrderGroupId(Long orderGroupId);
}
