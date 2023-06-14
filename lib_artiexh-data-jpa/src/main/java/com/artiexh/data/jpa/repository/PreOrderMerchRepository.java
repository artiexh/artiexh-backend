package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.PreOrderMerchEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface PreOrderMerchRepository extends JpaRepository<PreOrderMerchEntity, Long> {

	@Modifying
	@Transactional
	@Query(value = "insert into pre_order_merch (start_datetime, end_datetime, id) values (:startDateTime, :endDateTime, :id)", nativeQuery = true)
	void update(Instant startDateTime, Instant endDateTime, long id);
}
