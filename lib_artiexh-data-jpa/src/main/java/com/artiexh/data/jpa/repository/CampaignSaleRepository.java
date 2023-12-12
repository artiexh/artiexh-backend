package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.CampaignSaleEntity;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface CampaignSaleRepository
	extends JpaRepository<CampaignSaleEntity, Long>, JpaSpecificationExecutor<CampaignSaleEntity> {
	Optional<CampaignSaleEntity> findCampaignSaleEntityByIdAndOwnerId(Long id, Long ownerId);

	@QueryHints(value = {
		@QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
		@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
		@QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
	})
	@Query("select cs from CampaignSaleEntity cs where (cs.publicDate <= :currentTime or cs.from <= :currentTime) and cs.to >= :currentTime")
	Stream<CampaignSaleEntity> streamAllByFromBeforeAndToAfter(@Param("currentTime") Instant currentTime);

	@Modifying
	@Query("update CampaignSaleEntity cs set cs.status = cast(-1 as byte) where cs.to >= :closedTime")
	void closeExpiredSaleCampaigns(@Param("closedTime") Instant closedTime);
}
