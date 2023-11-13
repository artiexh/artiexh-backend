package com.artiexh.api.controller.campaign;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.data.jpa.entity.ProductHistoryEntity;
import com.artiexh.model.domain.ProductInventoryQuantity;
import com.artiexh.model.rest.campaign.request.FinalizeProductRequest;
import com.artiexh.model.rest.campaign.response.ProductInCampaignDetailResponse;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(Endpoint.Campaign.ROOT)
@RequiredArgsConstructor
public class CampaignController {
	private final CampaignService campaignService;
	@PostMapping("/{id}/finalize-product")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public Set<ProductResponse> finalizeProduct(@PathVariable("id") Long campaignId,
												@RequestBody @Valid Set<FinalizeProductRequest> request) {
		try {
			return campaignService.finalizeProduct(campaignId, request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@PostMapping("/{id}/publish-to-product-inventory")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public void publishProduct(
		Authentication authentication,
		@PathVariable("id") Long campaignId,
		@RequestBody @Valid Map<String, Long> productQuantities
	) {
		try {
			long staffId = (long) authentication.getPrincipal();
			campaignService.staffFinishManufactureCampaign(productQuantities, campaignId, staffId, null);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@GetMapping("/{id}/product/{product-id}")
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN','STAFF')")
	public ProductInCampaignDetailResponse getProductInCampaign(@PathVariable("product-id") Long productId,
																@PathVariable Long id) {
		try {
			return campaignService.getProductInCampaign(id, productId);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}
}
