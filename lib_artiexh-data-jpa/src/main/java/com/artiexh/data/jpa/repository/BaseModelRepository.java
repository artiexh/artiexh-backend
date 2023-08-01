package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.BaseModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseModelRepository extends JpaRepository<BaseModelEntity, Long>, JpaSpecificationExecutor<BaseModelEntity> {
}
