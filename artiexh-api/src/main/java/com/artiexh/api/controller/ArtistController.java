package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.service.ArtistService;
import com.artiexh.model.domain.Post;
import com.artiexh.model.mapper.PostMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.artist.filter.ProductPageFilter;
import com.artiexh.model.rest.artist.response.ArtistProfileResponse;
import com.artiexh.model.rest.order.request.OrderPageFilter;
import com.artiexh.model.rest.order.user.response.UserCampaignOrderResponse;
import com.artiexh.model.rest.order.user.response.UserCampaignOrderResponsePage;
import com.artiexh.model.rest.post.PostDetail;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Artist.ROOT)
public class ArtistController {
	private final ArtistService artistService;
	private final PostMapper postMapper;

	@GetMapping(Endpoint.Artist.ARTIST_PROFILE)
	public ArtistProfileResponse getProfile(@PathVariable String username) {
		try {
			return artistService.getProfile(username);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

	@GetMapping(Endpoint.Artist.ARTIST_PRODUCT)
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN')")
	public PageResponse<ProductResponse> getAllProduct(
		Authentication authentication,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject @Valid ProductPageFilter filter
	) {
		long userId = (long) authentication.getPrincipal();
		filter.setArtistId(userId);
		return artistService.getAllProducts(filter.getQuery(), paginationAndSortingRequest.getPageable());
	}

	@GetMapping(Endpoint.Artist.ARTIST_ORDER)
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN')")
	public PageResponse<UserCampaignOrderResponsePage> getAllOrder(
		Authentication authentication,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject @Valid OrderPageFilter filter
	) {
		long userId = (long) authentication.getPrincipal();
		return artistService.getAllOrder(filter.getSpecificationForArtist(userId), paginationAndSortingRequest.getPageable());
	}

	@GetMapping(Endpoint.Artist.ARTIST_ORDER + "/{id}")
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN')")
	public UserCampaignOrderResponse getOrderById(
		@PathVariable Long id,
		Authentication authentication
	) {
		try {
			long userId = (long) authentication.getPrincipal();
			return artistService.getOrderById(id, userId);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(ErrorCode.ORDER_IS_INVALID.getCode(), ErrorCode.ORDER_IS_INVALID.getMessage(), exception);
		}
	}

	@GetMapping(Endpoint.Artist.ARTIST_POST)
	public PageResponse<PostDetail> getArtistPost(@PathVariable long id,
												  @Valid @ParameterObject PaginationAndSortingRequest pagination) {
		try {
			Page<Post> posts = artistService.getArtistPost(id, pagination.getPageable());
			return new PageResponse<>(posts.map(postMapper::domainToDetail));
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}
}
