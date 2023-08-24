package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

import java.util.Collection;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

	@Modifying
	@Query("update ProductEntity entity set entity.status = cast(1 as byte) where entity.id = :id")
	void softDelete(long id);

	Set<ProductEntity> findAllByIdInAndShopIdAndStatus(Set<Long> ids, Long shopId, byte status);

	int countAllByIdInAndOwnerId(Collection<Long> id, Long owner_id);
}
