package com.artiexh.api.service;

import com.artiexh.model.domain.ProductCategory;
import com.artiexh.model.domain.ProductTag;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.category.ProductCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
	Page<ProductCategoryResponse> getInPage(Pageable pageable);
}
