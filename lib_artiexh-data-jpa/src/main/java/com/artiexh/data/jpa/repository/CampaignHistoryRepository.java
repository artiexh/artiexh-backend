package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.CampaignHistoryEntity;
import com.artiexh.data.jpa.entity.CampaignHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignHistoryRepository extends JpaRepository<CampaignHistoryEntity, CampaignHistoryId> {
}
