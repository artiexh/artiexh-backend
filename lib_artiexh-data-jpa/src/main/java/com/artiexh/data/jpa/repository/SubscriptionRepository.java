package com.artiexh.data.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, SubscriptionEntityId> {
    long countByArtistId(Long artistId);
}
