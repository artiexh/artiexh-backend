package com.artiexh.api.service.impl;

import com.artiexh.api.service.CategoryService;
import com.artiexh.data.jpa.repository.ProductCategoryRepository;
import com.artiexh.model.mapper.ProductCategoryMapper;
import com.artiexh.model.rest.category.ProductCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final ProductCategoryRepository categoryRepository;
	private final ProductCategoryMapper categoryMapper;

	@Override
	public Page<ProductCategoryResponse> getInPage(String name, Pageable pageable) {
		return categoryRepository.findAllByNameLike(name, pageable).map(categoryMapper::entityToResponse);
	}
}
