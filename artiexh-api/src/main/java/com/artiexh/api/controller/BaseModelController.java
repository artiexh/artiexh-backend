package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.BaseModelService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelFilter;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.BaseModel.ROOT)
public class BaseModelController {
	private BaseModelService baseModelService;

	@PostMapping
	public BaseModelDetail create(@RequestBody @Valid BaseModelDetail detail) {
		return baseModelService.create(detail);
	}

	@PutMapping(path = Endpoint.BaseModel.BASE_MODEL_DETAIL)
	public BaseModelDetail update(@PathVariable long id, @RequestBody @Valid BaseModelDetail detail) {
		detail.toBuilder()
			.id(id)
			.build();
		return baseModelService.create(detail);
	}

	@GetMapping
	public PageResponse<BaseModelInfo> getInPage(
		@ParameterObject @Valid BaseModelFilter filter,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest
	) {
		return baseModelService.getInPage(filter, paginationAndSortingRequest.getPageable());
	}

	@GetMapping(path = Endpoint.BaseModel.BASE_MODEL_DETAIL)
	public BaseModelDetail getDetail(@PathVariable long id) {
		return baseModelService.getDetail(id);
	}
}
