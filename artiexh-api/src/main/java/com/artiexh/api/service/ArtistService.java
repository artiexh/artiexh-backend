package com.artiexh.api.service;

import com.artiexh.model.domain.Post;
import com.artiexh.model.rest.artist.request.UpdateArtistProfileRequest;
import com.artiexh.model.rest.artist.response.ArtistProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistService {

	ArtistProfileResponse getProfile(long id);

	Page<ArtistProfileResponse> getAllProfile(Pageable pageable);

	ArtistProfileResponse getProfile(String username);

	//\PageResponse<ProductResponse> getAllProducts(Query query, Pageable pageable);

	//UserUserCampaignOrderDetailResponse getOrderById(Long orderId, Long artistId);

	//PageResponse<CampaignOrderResponsePage> getAllOrder(Specification<CampaignOrderEntity> specification, Pageable pageable);

	Page<Post> getArtistPost(Long artistId, Pageable pageable);

	ArtistProfileResponse updateArtistProfile(Long artistId, UpdateArtistProfileRequest request);
}
