package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductTagEntity;
import com.artiexh.data.jpa.entity.ProvidedProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvidedProductRepository extends JpaRepository<ProvidedProductEntity, Long>,
	JpaSpecificationExecutor<ProvidedProductEntity> {
}
