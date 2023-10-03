package com.artiexh.api.service.campaign;

import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.model.rest.campaign.request.CampaignRequest;
import com.artiexh.model.rest.campaign.request.UpdateCampaignStatusRequest;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CampaignService {

	CampaignResponse createCampaign(Long ownerId, CampaignRequest request);

	CampaignResponse updateCampaign(Long ownerId, CampaignRequest request);

	Page<CampaignResponse> getAllCampaigns(Specification<CampaignEntity> specification, Pageable pageable);

	CampaignResponse artistUpdateStatus(Long artistId, UpdateCampaignStatusRequest request);

	CampaignResponse reviewCampaign(Long staffId, UpdateCampaignStatusRequest request);
}
