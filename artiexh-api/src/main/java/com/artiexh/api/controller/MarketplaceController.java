package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.service.ArtistService;
import com.artiexh.api.service.campaign.CampaignService;
import com.artiexh.api.service.product.ProductService;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductSuggestion;
import com.artiexh.model.mapper.ArtistMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.artist.filter.ArtistCampaignFilter;
import com.artiexh.model.rest.artist.response.ArtistProfileResponse;
import com.artiexh.model.rest.campaign.request.CampaignRequestFilter;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import com.artiexh.model.rest.campaign.response.PublishedCampaignDetailResponse;
import com.artiexh.model.rest.product.request.GetAllProductFilter;
import com.artiexh.model.rest.product.request.GetCampaignProductFilter;
import com.artiexh.model.rest.product.request.SuggestionFilter;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Marketplace.ROOT)
public class MarketplaceController {
	private final CampaignService campaignService;

	private final ProductService productService;

	private final ProductMapper productMapper;

	private final ArtistService artistService;

	private final ArtistMapper artistMapper;

	@GetMapping("/campaign")
	public PageResponse<CampaignResponse> getAllPublicCampaign(@ParameterObject @Validated PaginationAndSortingRequest paginationAndSortingRequest,
															   @ParameterObject CampaignRequestFilter filter) {
		var response = campaignService.getAllCampaigns(filter.getMarketPlaceSpecification(), paginationAndSortingRequest.getPageable());
		return new PageResponse<>(response);
	}

	@GetMapping("/campaign/{id}")
	public PublishedCampaignDetailResponse getDetail(
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

	@GetMapping("/artist/{username}/product")
	public PageResponse<ProductResponse> getArtistProduct(
			@PathVariable String username,
			@ParameterObject @Valid PaginationAndSortingRequest pagination,
			@ParameterObject @Valid GetAllProductFilter filter
	) {
		try {
			filter.setUsername(username);
			Page<Product> productPage = productService.getInPage(filter.getQuery(), pagination.getPageable());
			return new PageResponse<>(productMapper.productPageToProductResponsePage(productPage));
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

	@GetMapping("/product")
	public PageResponse<ProductResponse> getInPage(
			@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
			@ParameterObject @Valid GetAllProductFilter filter
	) {
		Page<Product> productPage = productService.getInPage(
				filter.getQuery(),
				paginationAndSortingRequest.getPageable()
		);
		return new PageResponse<>(productMapper.productPageToProductResponsePage(productPage));
	}

	@GetMapping("/product/{id}")
	public ProductResponse getDetail(@PathVariable("id") long id) {
		Product product;
		try {
			product = productService.getDetail(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(ErrorCode.PRODUCT_NOT_FOUND.getCode(), ErrorCode.PRODUCT_NOT_FOUND.getMessage(), ex);
		}
		return productMapper.domainToProductResponse(product);
	}

	@GetMapping("/product/suggestion")
	public PageResponse<ProductSuggestion> getSuggestionInPage(
			@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
			@ParameterObject @Valid SuggestionFilter filter
	) {
		Page<ProductSuggestion> suggestionPage = productService.getSuggestionInPage(
				filter.getQuery(),
				paginationAndSortingRequest.getPageable()
		);
		return new PageResponse<>(suggestionPage);
	}

	@GetMapping("/artist")
	public PageResponse<ArtistProfileResponse> getInPage(
			@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest
	) {
		Page<ArtistProfileResponse> profiles = artistService.getAllProfile(
				paginationAndSortingRequest.getPageable()
		);
		return new PageResponse<>(profiles);
	}
}
