package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.ProductBaseService;
import com.artiexh.model.domain.ProductBase;
import com.artiexh.model.mapper.ProductBaseMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.productbase.ProductBaseDetail;
import com.artiexh.model.rest.productbase.ProductBaseFilter;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import com.artiexh.model.rest.productbase.request.UpdateProductBaseDetail;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.ProductBase.ROOT)
public class ProductBaseController {
	private final ProductBaseService productBaseService;
	private final ProductBaseMapper mapper;

	//Create Product Base
	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProductBaseDetail create(@Valid @RequestBody ProductBaseDetail detail) {
		try {
			ProductBase productBase = mapper.detailToDomain(detail);
			productBase = productBaseService.create(productBase);
			return mapper.domainToDetail(productBase);
		} catch (IllegalArgumentException | EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
		}
	}

	@PutMapping(Endpoint.ProductBase.DETAIL)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ProductBaseDetail update(@Valid @RequestBody UpdateProductBaseDetail detail) {
		try {
			ProductBase productBase = mapper.detailToDomain(detail);
			productBase = productBaseService.update(productBase);
			return mapper.domainToDetail(productBase);
		} catch (IllegalArgumentException | EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
		}
	}

	//Get Product Base Page

	@GetMapping
	public PageResponse<ProductBaseInfo> getInPage(
		@ParameterObject ProductBaseFilter filter,
		@Valid @ParameterObject PaginationAndSortingRequest pagination) {
		Page<ProductBase> productPage = productBaseService.getInPage(filter.getSpecification(), pagination.getPageable());
		return new PageResponse<>(productPage.map(mapper::domainToInfo));
	}

	@GetMapping(Endpoint.ProductBase.DETAIL)
	public ProductBaseDetail getById(@PathVariable("id") Long id) {
		try {
			ProductBase productBase = productBaseService.getById(id);
			return mapper.domainToDetail(productBase);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
		}

	}
}
