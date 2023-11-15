package com.artiexh.api.controller.campaign;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.api.service.provider.ProviderService;
import com.artiexh.model.domain.CampaignHistory;
import com.artiexh.model.domain.Role;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.campaign.request.*;
import com.artiexh.model.rest.campaign.response.CampaignDetailResponse;
import com.artiexh.model.rest.campaign.response.CampaignProviderResponse;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.artiexh.model.domain.CampaignStatus.ALLOWED_ADMIN_VIEW_STATUS;

@RestController
@RequestMapping(Endpoint.ArtistCampaign.ROOT)
@RequiredArgsConstructor
public class ArtistCampaignController {
	private final CampaignService campaignService;
	private final ProviderService providerService;
	@Value("${artiexh.security.admin.id}")
	private Long rootAdminId;

	@PostMapping
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN','STAFF')")
	public CampaignDetailResponse createCampaign(Authentication authentication,
												 @RequestBody @Validated ArtistCampaignRequest request) {
		var role = extractRole(authentication)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no role"));
		long ownerId = role == Role.STAFF ? rootAdminId : (long) authentication.getPrincipal();

		try {
			return campaignService.createCampaign(ownerId, request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN','STAFF')")
	public CampaignDetailResponse updateCampaign(Authentication authentication,
												 @PathVariable Long id,
												 @RequestBody @Validated ArtistCampaignRequest request) {
		var role = extractRole(authentication)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no role"));
		long ownerId = role == Role.STAFF ? rootAdminId : (long) authentication.getPrincipal();

		request.setId(id);
		try {
			return campaignService.updateCampaign(ownerId, request);
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
		var role = extractRole(authentication)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no role"));

		Long userId = (Long) authentication.getPrincipal();
		request.setId(id);

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
	public Set<CampaignProviderResponse> getProviderSupportCustomProducts(Authentication authentication,
																		  @ParameterObject @RequestParam Set<Long> customProductIds) {
		long artistId = (long) authentication.getPrincipal();
		try {
			return providerService.getAllSupportedCustomProducts(artistId, customProductIds);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN','STAFF')")
	public PageResponse<CampaignResponse> getAllCampaigns(Authentication authentication,
														  @ParameterObject @Validated PaginationAndSortingRequest paginationAndSortingRequest,
														  @ParameterObject CampaignRequestFilter filter) {
		var role = extractRole(authentication)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no role"));
		Long userId = (Long) authentication.getPrincipal();

		if (role == Role.ARTIST) {
			if (filter.getOwnerId() != null && !filter.getOwnerId().equals(userId.toString())) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only get your own campaigns");
			} else {
				filter.setOwnerId(userId.toString());
			}
		} else { // ADMIN and STAFF
			if (filter.getStatus() == null || filter.getStatus().isEmpty()) {
				filter.setStatus(ALLOWED_ADMIN_VIEW_STATUS);
			} else if (!ALLOWED_ADMIN_VIEW_STATUS.containsAll(filter.getStatus())) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only get campaigns after submitted");
			}
		}

		var response = campaignService.getAllCampaigns(filter.getSpecification(), paginationAndSortingRequest.getPageable());
		return new PageResponse<>(response);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN','STAFF')")
	public CampaignDetailResponse getCampaign(Authentication authentication,
											  @PathVariable Long id) {
		var role = extractRole(authentication)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no role"));
		long userId = role == Role.STAFF ? rootAdminId : (long) authentication.getPrincipal();

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

	@GetMapping("/{id}/campaign-history/top-5")
	public List<CampaignHistory> getCampaignHistory(
		@PathVariable Long id
	) {
		try {
			PaginationAndSortingRequest pagination = new PaginationAndSortingRequest();
			pagination.setSortBy("id.eventTime");
			return campaignService.getCampaignHistory(id, pagination.getPageable());
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	private Optional<Role> extractRole(Authentication authentication) {
		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.findFirst()
			.map(Role::valueOf);
	}
}
