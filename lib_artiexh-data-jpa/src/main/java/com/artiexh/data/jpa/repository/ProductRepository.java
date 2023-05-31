package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.MerchEntity;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.annotations.OnDelete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<MerchEntity, Long>, JpaSpecificationExecutor<MerchEntity> {
	Page<MerchEntity> findAll(Specification<MerchEntity> specification, Pageable pageable);
	List<MerchEntity> findAll(Specification<MerchEntity> specification);
	@Modifying
	@Query("update MerchEntity entity set entity.isDeleted = false where entity.id=:id")
	void delete(long id);
}
