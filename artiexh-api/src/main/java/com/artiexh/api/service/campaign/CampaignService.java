package com.artiexh.api.service.campaign;

import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.model.rest.campaign.request.CreateCampaignRequest;
import com.artiexh.model.rest.campaign.request.UpdateCampaignRequest;
import com.artiexh.model.rest.campaign.response.CreateCampaignResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CampaignService {

	CreateCampaignResponse createCampaign(Long ownerId, CreateCampaignRequest request);

	CreateCampaignResponse updateCampaign(Long ownerId, UpdateCampaignRequest request);

	Page<CreateCampaignResponse> getAllCampaigns(Specification<CampaignEntity> specification, Pageable pageable);

}
