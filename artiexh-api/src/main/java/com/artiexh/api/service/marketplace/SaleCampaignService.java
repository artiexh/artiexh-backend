package com.artiexh.api.service.marketplace;

import com.artiexh.data.jpa.entity.CampaignSaleEntity;
import com.artiexh.model.rest.marketplace.request.SaleCampaignRequest;
import com.artiexh.model.rest.marketplace.response.SaleCampaignDetailResponse;
import com.artiexh.model.rest.marketplace.response.SaleCampaignResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SaleCampaignService {

	SaleCampaignDetailResponse createSaleCampaign(long creatorId, SaleCampaignRequest request);

	Page<SaleCampaignResponse> getAll(Pageable pageable, Specification<CampaignSaleEntity> specification);

	SaleCampaignDetailResponse getDetail(Long id);
}
