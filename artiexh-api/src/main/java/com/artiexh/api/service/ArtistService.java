package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.artist.ShopOrderResponsePage;
import com.artiexh.model.rest.product.response.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;

public interface ArtistService {
	PageResponse<ProductResponse> getAllProducts (Query query, Pageable pageable);
	PageResponse<ShopOrderResponsePage> getAllOrder (Specification<OrderEntity> specification, Pageable pageable);
}
