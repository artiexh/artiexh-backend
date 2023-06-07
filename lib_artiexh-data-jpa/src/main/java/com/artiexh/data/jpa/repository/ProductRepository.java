package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.MerchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<MerchEntity, Long>, JpaSpecificationExecutor<MerchEntity> {

	@Modifying
	@Query("update MerchEntity entity set entity.status = :deleted where entity.id=:id")
	void delete(long id, Byte deleted);
}
