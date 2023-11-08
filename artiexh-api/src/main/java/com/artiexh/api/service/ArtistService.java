package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.CampaignOrderEntity;
import com.artiexh.model.domain.Post;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.artist.request.UpdateArtistProfileRequest;
import com.artiexh.model.rest.artist.response.ArtistProfileResponse;
import com.artiexh.model.rest.order.user.response.CampaignOrderResponsePage;
import com.artiexh.model.rest.order.user.response.UserUserCampaignOrderDetailResponse;
import com.artiexh.model.rest.product.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;

public interface ArtistService {

	ArtistProfileResponse getProfile(long id);

	ArtistProfileResponse getProfile(String username);

	PageResponse<ProductResponse> getAllProducts(Query query, Pageable pageable);

	UserUserCampaignOrderDetailResponse getOrderById(Long orderId, Long artistId);

	PageResponse<CampaignOrderResponsePage> getAllOrder(Specification<CampaignOrderEntity> specification, Pageable pageable);

	Page<Post> getArtistPost(Long artistId, Pageable pageable);

	ArtistProfileResponse updateArtistProfile(Long artistId, UpdateArtistProfileRequest request);
}
