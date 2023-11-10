package com.artiexh.api.service.campaign;

import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.model.domain.ProductInventoryQuantity;
import com.artiexh.model.rest.campaign.request.*;
import com.artiexh.model.rest.campaign.response.*;
import com.artiexh.model.rest.product.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public interface CampaignService {

	CampaignDetailResponse createCampaign(Long ownerId, ArtistCampaignRequest request);

	CampaignDetailResponse createPublicCampaign(Long createdBy, CreatePublicCampaignRequest request);

	CampaignDetailResponse updateCampaign(Long ownerId, ArtistCampaignRequest request);

	Page<CampaignResponse> getAllCampaigns(Specification<CampaignEntity> specification, Pageable pageable);

	CampaignDetailResponse getCampaignDetail(Long userId, Long campaignId);

	PublishedCampaignDetailResponse getCampaignDetail(Long campaignId);

	CampaignResponse artistUpdateStatus(Long artistId, UpdateCampaignStatusRequest request);

	CampaignResponse reviewCampaign(Long staffId, UpdateCampaignStatusRequest request);

	Set<ProductResponse> finalizeProduct(Long campaignId, Set<FinalizeProductRequest> request);

	void staffPublishProductCampaign(Long campaignId, boolean isPrePublished, Long userId);
	Page<ProductInCampaignResponse> getAllProductCampaign(Long campaignId, Pageable pageable);

	ProductInCampaignDetailResponse getProductInCampaign(Long campaignId, Long productId);

	void staffFinishManufactureCampaign(Set<ProductInventoryQuantity> productInventoryQuantities,
										Long campaignId,
										Long staffId,
										String message);
}
