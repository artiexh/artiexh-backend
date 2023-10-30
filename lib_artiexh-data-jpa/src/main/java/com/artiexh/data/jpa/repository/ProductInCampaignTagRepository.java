package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductInCampaignTagEntity;
import com.artiexh.data.jpa.entity.ProductInCampaignTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInCampaignTagRepository extends JpaRepository<ProductInCampaignTagEntity, ProductInCampaignTagId> {
}