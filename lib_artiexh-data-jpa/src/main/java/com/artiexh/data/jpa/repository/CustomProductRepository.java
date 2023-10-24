package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.CustomProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomProductRepository extends JpaRepository<CustomProductEntity, Long> {
	Page<CustomProductEntity> findAllByCampaignId(Long campaignId, Pageable pageable);
}
