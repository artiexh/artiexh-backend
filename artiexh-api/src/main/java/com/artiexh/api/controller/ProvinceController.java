package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.AddressService;
import com.artiexh.api.service.ProductService;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.Province;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.address.ProvinceResponse;
import com.artiexh.model.rest.product.request.GetAllProductFilter;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.Province.ROOT)
public class ProvinceController {
	private final AddressService addressService;
	@GetMapping
	public PageResponse<ProvinceResponse> getInPage(
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest
	) {
		Page<ProvinceResponse> provincePage = addressService.getInPage(
			paginationAndSortingRequest.getPageable()
		);
		return new PageResponse<>(provincePage);
	}
}
