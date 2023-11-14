package com.artiexh.api.controller.marketplace;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.marketplace.SaleCampaignService;
import com.artiexh.model.rest.marketplace.salecampaign.request.SaleCampaignRequest;
import com.artiexh.model.rest.marketplace.salecampaign.response.CampaignStatistics;
import com.artiexh.model.rest.marketplace.salecampaign.response.SaleCampaignDetailResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = Endpoint.SaleCampaign.ROOT)
public class SaleCampaignController {
	private final SaleCampaignService saleCampaignService;

	@PostMapping
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public SaleCampaignDetailResponse createSaleCampaign(Authentication authentication,
														 @RequestBody SaleCampaignRequest request) {
		Long id = (Long) authentication.getPrincipal();
		try {
			return saleCampaignService.createSaleCampaign(id, request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@GetMapping("/{id}/statistics")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF','ARTIST')")
	public CampaignStatistics getStatistic(
		@PathVariable("id") Long id
	) {
		try {
			return saleCampaignService.getStatistics(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

}
