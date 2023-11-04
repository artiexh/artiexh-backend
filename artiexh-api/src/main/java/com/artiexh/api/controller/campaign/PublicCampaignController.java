package com.artiexh.api.controller.campaign;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.model.rest.campaign.request.CreatePublicCampaignRequest;
import com.artiexh.model.rest.campaign.response.CampaignDetailResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoint.PublicCampaign.ROOT)
@RequiredArgsConstructor
public class PublicCampaignController {
	private final CampaignService campaignService;

	@PostMapping
	@PreAuthorize("hasAnyAuthority('STAFF','ADMIN')")
	public CampaignDetailResponse createPublicCampaign(
		Authentication authentication,
		@RequestBody @Valid CreatePublicCampaignRequest request
	) {
		long createdBy = (long) authentication.getPrincipal();
		return campaignService.createPublicCampaign(createdBy, request);
	}

}
