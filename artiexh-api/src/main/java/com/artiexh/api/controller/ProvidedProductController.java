package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProvidedProductService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelFilter;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import com.artiexh.model.rest.providedproduct.ProvidedProductDetail;
import com.artiexh.model.rest.providedproduct.ProvidedProductFilter;
import com.artiexh.model.rest.providedproduct.ProvidedProductInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.ProvidedProduct.ROOT)
public class ProvidedProductController {
	private final ProvidedProductService service;
	@PostMapping
	public ProvidedProductDetail create(@RequestBody @Valid ProvidedProductDetail detail) {
		return service.create(detail);
	}

	@PutMapping()
	public ProvidedProductDetail update(@RequestBody @Valid ProvidedProductDetail detail) {

		try {
			return service.update(detail);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(
				BAD_REQUEST,
				ErrorCode.BASE_MODEL_NOT_FOUND.getMessage(),
				exception);
		}
	}

	@GetMapping
	public PageResponse<ProvidedProductInfo> getInPage(
//		@ParameterObject @Valid ProvidedProductFilter filter,
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest
	) {
		return service.getInPage(null, paginationAndSortingRequest.getPageable());
	}

//	@GetMapping()
//	public ProvidedProductDetail getDetail(long baseModelId, String providerId) {
//		try {
//			return service.getDetail(baseModelId, providerId);
//		} catch (EntityNotFoundException exception) {
//			throw new ResponseStatusException(
//				ErrorCode.BASE_MODEL_NOT_FOUND.getCode(),
//				ErrorCode.BASE_MODEL_NOT_FOUND.getMessage(),
//				exception);
//		}
//	}
}
