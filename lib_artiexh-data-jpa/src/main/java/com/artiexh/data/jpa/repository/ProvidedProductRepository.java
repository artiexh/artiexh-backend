package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProvidedModelEntity;
import com.artiexh.data.jpa.entity.ProvidedModelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvidedProductRepository extends JpaRepository<ProvidedModelEntity, ProvidedModelId>,
	JpaSpecificationExecutor<ProvidedModelEntity> {
}
