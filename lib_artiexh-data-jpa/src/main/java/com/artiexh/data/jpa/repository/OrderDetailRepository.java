package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.OrderDetailEntity;
import com.artiexh.data.jpa.entity.OrderDetailEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, OrderDetailEntityId> {
}
