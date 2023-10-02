package com.artiexh.api.controller.campaign;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.model.domain.Role;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.campaign.request.CampaignRequestFilter;
import com.artiexh.model.rest.campaign.request.CreateCampaignRequest;
import com.artiexh.model.rest.campaign.response.CreateCampaignResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(Endpoint.Campaign.ROOT)
@RequiredArgsConstructor
public class CampaignController {
	private final CampaignService campaignService;

	@PostMapping
	@PreAuthorize("hasAuthority('ARTIST')")
	public CreateCampaignResponse createCampaign(Authentication authentication,
												 @RequestBody @Validated CreateCampaignRequest request) {
		long artistId = (long) authentication.getPrincipal();
		try {
			return campaignService.createCampaign(artistId, request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN','STAFF')")
	public PageResponse<CreateCampaignResponse> getAllCampaigns(Authentication authentication,
																@ParameterObject @Validated PaginationAndSortingRequest paginationAndSortingRequest,
																@ParameterObject CampaignRequestFilter filter) {
		var role = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.findFirst()
			.map(Role::valueOf)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no role"));

		Long userId = (Long) authentication.getPrincipal();

		if (role == Role.ARTIST) {
			if (filter.getOwnerId() != null && !filter.getOwnerId().equals(userId.toString())) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only get your own campaigns");
			} else {
				filter.setOwnerId(userId.toString());
			}
		}

		var response = campaignService.getAllCampaigns(filter.getSpecification(), paginationAndSortingRequest.getPageable());
		return new PageResponse<>(response);
	}

}
