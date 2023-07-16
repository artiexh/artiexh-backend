package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.BaseModelService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelFilter;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.BaseModel.ROOT)
public class BaseModelController {
	private final BaseModelService baseModelService;

	@PostMapping
	public BaseModelDetail create(@RequestBody @Valid BaseModelDetail detail) {
		return baseModelService.create(detail);
	}

	@PutMapping(path = Endpoint.BaseModel.BASE_MODEL_DETAIL)
	public BaseModelDetail update(@PathVariable long id, @RequestBody @Valid BaseModelDetail detail) {
		detail.setId(id);
		try {
			return baseModelService.update(detail);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(
				BAD_REQUEST,
				ErrorCode.BASE_MODEL_NOT_FOUND.getMessage(),
				exception);
		}
	}

	@GetMapping
	public PageResponse<BaseModelInfo> getInPage(
		@ParameterObject @Valid BaseModelFilter filter,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest
	) {
		return baseModelService.getInPage(filter.getSpecification(), paginationAndSortingRequest.getPageable());
	}

	@GetMapping(path = Endpoint.BaseModel.BASE_MODEL_DETAIL)
	public BaseModelDetail getDetail(@PathVariable long id) {
		try {
			return baseModelService.getDetail(id);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(
				ErrorCode.BASE_MODEL_NOT_FOUND.getCode(),
				ErrorCode.BASE_MODEL_NOT_FOUND.getMessage(),
				exception);
		}

	}
}
