package com.artiexh.api.controller.marketplace;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.marketplace.SaleCampaignService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.marketplace.salecampaign.filter.SaleCampaignFilter;
import com.artiexh.model.rest.marketplace.salecampaign.request.ProductInSaleRequest;
import com.artiexh.model.rest.marketplace.salecampaign.request.SaleCampaignRequest;
import com.artiexh.model.rest.marketplace.salecampaign.request.UpdateProductInSaleRequest;
import com.artiexh.model.rest.marketplace.salecampaign.response.CampaignStatistics;
import com.artiexh.model.rest.marketplace.salecampaign.response.ProductInSaleCampaignResponse;
import com.artiexh.model.rest.marketplace.salecampaign.response.SaleCampaignDetailResponse;
import com.artiexh.model.rest.marketplace.salecampaign.response.SaleCampaignResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = Endpoint.SaleCampaign.ROOT)
public class SaleCampaignController {
	private final SaleCampaignService saleCampaignService;

	@PostMapping
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public SaleCampaignDetailResponse createSaleCampaign(Authentication authentication,
														 @RequestBody @Validated SaleCampaignRequest request) {
		Long id = (Long) authentication.getPrincipal();
		try {
			return saleCampaignService.createSaleCampaign(id, request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public SaleCampaignDetailResponse updateSaleCampaign(@PathVariable("id") Long id,
														 @RequestBody @Validated SaleCampaignRequest request) {
		try {
			return saleCampaignService.updateSaleCampaign(id, request);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@PostMapping("/{id}/product-in-sale")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public Set<ProductInSaleCampaignResponse> createProductInSale(@PathVariable Long id,
																  @RequestBody @Validated Set<ProductInSaleRequest> requests) {
		try {
			return saleCampaignService.createProductInSaleCampaign(id, requests);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@PutMapping("/{id}/product-in-sale/{productCode}")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public ProductInSaleCampaignResponse updateProductInSale(@PathVariable Long id,
															 @PathVariable String productCode,
															 @RequestBody @Validated UpdateProductInSaleRequest request) {
		try {
			return saleCampaignService.updateProductInSaleCampaign(id, productCode, request);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@DeleteMapping("/{id}/product-in-sale")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public void deleteProductInSale(@PathVariable("id") Long campaignId,
									@RequestBody @Validated Set<String> productCodes) {
		try {
			saleCampaignService.deleteProductInSaleCampaign(campaignId, productCodes);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
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

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public SaleCampaignDetailResponse getDetail(
		@PathVariable("id") Long id
	) {
		try {
			return saleCampaignService.getDetail(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@GetMapping()
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public PageResponse<SaleCampaignResponse> getAllSaleCampaign(@ParameterObject @Validated PaginationAndSortingRequest paginationAndSortingRequest,
																 @ParameterObject SaleCampaignFilter filter) {
		return new PageResponse<>(saleCampaignService.getAll(
			paginationAndSortingRequest.getPageable(),
			filter.getSpecification())
		);
	}
}
