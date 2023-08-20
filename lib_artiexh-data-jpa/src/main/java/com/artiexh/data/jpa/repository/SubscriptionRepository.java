package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.SubscriptionEntity;
import com.artiexh.data.jpa.entity.SubscriptionEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, SubscriptionEntityId> {
	long countByArtistId(Long artistId);
}
