package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.data.jpa.entity.CartItemEntity;
import com.artiexh.data.jpa.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseModelRepository extends JpaRepository<BaseModelEntity, Long> {
}
