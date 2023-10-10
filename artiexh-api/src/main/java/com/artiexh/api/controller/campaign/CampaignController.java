package com.artiexh.api.controller.campaign;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.api.service.product.ProductService;
import com.artiexh.api.service.provider.ProviderService;
import com.artiexh.model.domain.Role;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.campaign.request.CampaignRequest;
import com.artiexh.model.rest.campaign.request.CampaignRequestFilter;
import com.artiexh.model.rest.campaign.request.PublishProductRequest;
import com.artiexh.model.rest.campaign.request.UpdateCampaignStatusRequest;
import com.artiexh.model.rest.campaign.response.CampaignDetailResponse;
import com.artiexh.model.rest.campaign.response.CampaignProviderResponse;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping(Endpoint.Campaign.ROOT)
@RequiredArgsConstructor
public class CampaignController {
	private final CampaignService campaignService;
	private final ProviderService providerService;
	private final ProductService productService;

	@PostMapping
	@PreAuthorize("hasAuthority('ARTIST')")
	public CampaignDetailResponse createCampaign(Authentication authentication,
												 @RequestBody @Validated CampaignRequest request) {
		long artistId = (long) authentication.getPrincipal();
		try {
			return campaignService.createCampaign(artistId, request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ARTIST')")
	public CampaignDetailResponse updateCampaign(Authentication authentication,
												 @PathVariable Long id,
												 @RequestBody @Validated CampaignRequest request) {
		long artistId = (long) authentication.getPrincipal();
		request.setId(id);
		try {
			return campaignService.updateCampaign(artistId, request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN','STAFF')")
	public CampaignResponse updateStatus(Authentication authentication,
										 @PathVariable Long id,
										 @RequestBody @Validated UpdateCampaignStatusRequest request) {
		request.setId(id);

		var role = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.findFirst()
			.map(Role::valueOf)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no role"));

		Long userId = (Long) authentication.getPrincipal();

		try {
			return switch (role) {
				case ARTIST -> campaignService.artistUpdateStatus(userId, request);
				case ADMIN, STAFF -> campaignService.reviewCampaign(userId, request);
				default -> throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no role");
			};
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		} catch (UnsupportedOperationException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
		}

	}

	@GetMapping("/provider")
	@PreAuthorize("hasAuthority('ARTIST')")
	public Set<CampaignProviderResponse> getProviderSupportInventoryItems(Authentication authentication,
																		  @ParameterObject @RequestParam Set<Long> inventoryItemIds) {
		long artistId = (long) authentication.getPrincipal();
		try {
			return providerService.getAllSupportedInventoryItems(artistId, inventoryItemIds);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN','STAFF')")
	public PageResponse<CampaignResponse> getAllCampaigns(Authentication authentication,
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

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN','STAFF')")
	public CampaignDetailResponse getCampaign(Authentication authentication,
											  @PathVariable Long id) {
		Long userId = (Long) authentication.getPrincipal();

		try {
			return campaignService.getCampaignDetail(userId, id);
		} catch (UsernameNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage(), ex);
		}
	}

	@PostMapping("/{id}/product")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public Set<ProductResponse> publishProduct(@PathVariable("id") Long campaignId,
										  @RequestBody @Valid Set<PublishProductRequest> request) {
		try {
			return campaignService.publishProduct(campaignId, request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}
}
