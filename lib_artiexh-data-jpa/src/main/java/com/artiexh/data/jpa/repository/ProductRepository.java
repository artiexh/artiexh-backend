package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductEntityId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, ProductEntityId>, JpaSpecificationExecutor<ProductEntity> {

	Page<ProductEntity> findByIdIn(Collection<ProductEntityId> id, Pageable pageable);

//	@Modifying
//	@Query("update ProductEntity entity set entity.status = cast(1 as byte) where entity.id = :id")
//	void softDelete(long id);
//
//	Set<ProductEntity> findAllByIdInAndShopIdAndStatus(Set<Long> ids, Long shopId, byte status);
//
//	Set<ProductEntity> findAllByIdIn(Set<Long> ids);
//
//	int countAllByIdInAndOwnerId(Collection<Long> id, Long ownerId);
//
//	int countAllByIdIn(Collection<Long> id);

//	@QueryHints(value = {
//		@QueryHint(name = HINT_FETCH_SIZE, value = "1"),
//		@QueryHint(name = HINT_CACHEABLE, value = "false"),
//		@QueryHint(name = HINT_READ_ONLY, value = "true")
//	})
//	@Query("select p from ProductEntity p where p.status = 2")
//	Stream<ProductEntity> streamAllByAvailableStatus();
}
