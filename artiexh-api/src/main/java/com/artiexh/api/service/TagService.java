package com.artiexh.api.service;

import com.artiexh.model.rest.tag.ProductTagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface TagService {
	Page<ProductTagResponse> getInPage(Set<String> queryParam, Pageable pageable);
}
