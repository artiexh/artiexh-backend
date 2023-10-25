package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.campaign.request.CampaignRequestFilter;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Marketplace.ROOT)
public class MarketplaceController {
	private final CampaignService campaignService;


	@GetMapping("/campaign")
	public PageResponse<CampaignResponse> getAllPublicCampaign(@ParameterObject @Validated PaginationAndSortingRequest paginationAndSortingRequest,
															   @ParameterObject CampaignRequestFilter filter) {
		var response = campaignService.getAllCampaigns(filter.getSpecification(), paginationAndSortingRequest.getPageable());
		return new PageResponse<>(response);
	}

}
