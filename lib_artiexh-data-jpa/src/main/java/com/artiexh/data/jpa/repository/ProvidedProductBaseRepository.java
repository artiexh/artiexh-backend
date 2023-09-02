package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvidedProductBaseRepository extends JpaRepository<ProvidedProductBaseEntity, ProvidedProductBaseId>,
	JpaSpecificationExecutor<ProvidedProductBaseEntity> {
}
