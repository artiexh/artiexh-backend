package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.api.service.product.ProductService;
import com.artiexh.model.domain.Product;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.artist.filter.ArtistCampaignFilter;
import com.artiexh.model.rest.campaign.request.CampaignRequestFilter;
import com.artiexh.model.rest.campaign.request.CreatePublicCampaignRequest;
import com.artiexh.model.rest.campaign.response.CampaignDetailResponse;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import com.artiexh.model.rest.campaign.response.ProductInCampaignResponse;
import com.artiexh.model.rest.campaign.response.PublishedCampaignDetailResponse;
import com.artiexh.model.rest.product.request.GetCampaignProductFilter;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Marketplace.ROOT)
public class MarketplaceController {
	private final CampaignService campaignService;

	private final ProductService productService;

	private final ProductMapper productMapper;
	@GetMapping("/campaign")
	public PageResponse<CampaignResponse> getAllPublicCampaign(@ParameterObject @Validated PaginationAndSortingRequest paginationAndSortingRequest,
															   @ParameterObject CampaignRequestFilter filter) {
		var response = campaignService.getAllCampaigns(filter.getMarketPlaceSpecification(), paginationAndSortingRequest.getPageable());
		return new PageResponse<>(response);
	}

	@PostMapping("/campaign")
	@PreAuthorize("hasAnyAuthority('STAFF','ADMIN')")
	public CampaignDetailResponse createPublicCampaign(
		Authentication authentication,
		@RequestBody @Valid CreatePublicCampaignRequest request
	) {
		long createdBy = (long) authentication.getPrincipal();
		return campaignService.createPublicCampaign(createdBy, request);
	}

	@GetMapping("/campaign/{id}")
	public PublishedCampaignDetailResponse getDetail(
		@ParameterObject @Validated PaginationAndSortingRequest paginationAndSortingRequest,
		@PathVariable Long id) {
		try {
			return campaignService.getCampaignDetail(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}

	}

	@GetMapping("/campaign/{id}/product")
	public PageResponse<ProductResponse> getShopProductCampaign(
		@ParameterObject @Valid PaginationAndSortingRequest pagination,
		@ParameterObject @Valid GetCampaignProductFilter filter,
		@PathVariable("id") Long campaignId) {
		try {
			filter.setCampaignId(campaignId);
			Page<Product> productCampaign = productService.getInPage(filter.getQuery(), pagination.getPageable());
			return new PageResponse<>(productMapper.productPageToProductResponsePage(productCampaign));
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

	@GetMapping("/artist/{username}/campaign")
	public PageResponse<CampaignResponse> getArtistCampaign(
		@PathVariable String username,
		@ParameterObject @Valid PaginationAndSortingRequest pagination,
		@ParameterObject @Valid ArtistCampaignFilter filter
	) {
		try {
			filter.setUsername(username);
			Page<CampaignResponse> campaignPage = campaignService.getAllCampaigns(filter.getSpecification(), pagination.getPageable());
			return new PageResponse<>(campaignPage);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}
}
