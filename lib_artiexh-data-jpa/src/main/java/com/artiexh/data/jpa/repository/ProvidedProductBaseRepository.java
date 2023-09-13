package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvidedProductBaseRepository extends JpaRepository<ProvidedProductBaseEntity, Long>,
	JpaSpecificationExecutor<ProvidedProductBaseEntity> {

	Optional<ProvidedProductBaseEntity> findByProvidedProductBaseId(ProvidedProductBaseId providedProductBaseId);
}
