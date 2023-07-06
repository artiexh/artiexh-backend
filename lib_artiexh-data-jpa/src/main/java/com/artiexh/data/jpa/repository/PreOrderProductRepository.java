package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.PreOrderProductEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface PreOrderProductRepository extends JpaRepository<PreOrderProductEntity, Long> {

	@Modifying
	@Transactional
	@Query(value = "insert into pre_order_product (start_datetime, end_datetime, id) values (:startDateTime, :endDateTime, :id)", nativeQuery = true)
	void update(Instant startDateTime, Instant endDateTime, long id);
}
