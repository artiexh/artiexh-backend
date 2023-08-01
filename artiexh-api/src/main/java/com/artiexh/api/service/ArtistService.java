package com.artiexh.api.service;

import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.product.response.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;

public interface ArtistService {
	PageResponse<ProductResponse> getAllProducts(Query query, Pageable pageable);
}
