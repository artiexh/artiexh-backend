package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductBaseRepository extends JpaRepository<ProductBaseEntity, Long>, JpaSpecificationExecutor<ProductBaseEntity> {
}
