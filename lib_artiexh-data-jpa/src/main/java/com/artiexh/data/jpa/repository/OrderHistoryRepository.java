package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.OrderHistoryEntity;
import com.artiexh.data.jpa.entity.OrderHistoryEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, OrderHistoryEntityId> {
}
