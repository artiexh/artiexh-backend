package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.ProvidedModelService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.providedproduct.ProvidedModelDetail;
import com.artiexh.model.rest.providedproduct.ProvidedModelFilter;
import com.artiexh.model.rest.providedproduct.ProvidedModelInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.ProvidedProduct.ROOT)
public class ProvidedModelController {
	private final ProvidedModelService service;

	@GetMapping
	public PageResponse<ProvidedModelInfo> getInPage(
		@ParameterObject @Valid ProvidedModelFilter filter,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest
	) {
		return service.getInPage(filter.getSpecification(), paginationAndSortingRequest.getPageable());
	}

}
