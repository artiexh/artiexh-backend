package com.artiexh.api.service.campaign;

import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.model.rest.campaign.request.CampaignRequest;
import com.artiexh.model.rest.campaign.request.PublishProductRequest;
import com.artiexh.model.rest.campaign.request.UpdateCampaignStatusRequest;
import com.artiexh.model.rest.campaign.response.CampaignDetailResponse;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import com.artiexh.model.rest.product.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CampaignService {

	CampaignDetailResponse createCampaign(Long ownerId, CampaignRequest request);

	CampaignDetailResponse updateCampaign(Long ownerId, CampaignRequest request);

	Page<CampaignResponse> getAllCampaigns(Specification<CampaignEntity> specification, Pageable pageable);

	CampaignDetailResponse getCampaignDetail(Long userId, Long campaignId);

	CampaignDetailResponse artistUpdateStatus(Long artistId, UpdateCampaignStatusRequest request);

	CampaignDetailResponse reviewCampaign(Long staffId, UpdateCampaignStatusRequest request);

	ProductResponse publishProduct(PublishProductRequest request);
}
