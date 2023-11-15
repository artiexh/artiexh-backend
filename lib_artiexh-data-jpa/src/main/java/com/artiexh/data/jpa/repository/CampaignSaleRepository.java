package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.CampaignSaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.time.Instant;
import java.util.stream.Stream;

@Repository
public interface CampaignSaleRepository
	extends JpaRepository<CampaignSaleEntity, Long>, JpaSpecificationExecutor<CampaignSaleEntity> {
	Optional<CampaignSaleEntity> findCampaignSaleEntityByIdAndOwnerId(Long id, Long ownerId);

	Stream<CampaignSaleEntity> streamALlByFromBeforeAndToAfter(Instant from, Instant to);
}
