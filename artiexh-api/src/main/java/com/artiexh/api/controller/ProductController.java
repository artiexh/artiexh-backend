package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.ProductService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.product.GetAllProductFilter;
import com.artiexh.model.rest.product.ProductDetail;
import com.artiexh.model.rest.product.response.ProductInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.Product.ROOT)
public class ProductController {
	private final ProductService productService;

	@GetMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public ProductDetail getDetail(@PathVariable("id") long id) {
		ProductDetail product = productService.getDetail(id);
		return product;
	}

	@PutMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public ProductDetail update(
		@PathVariable("id") long id,
		@RequestBody @Valid ProductDetail request) {
		request.setId(id);
		ProductDetail product = productService.update(request);
		return product;
	}

	@PostMapping
	public ProductDetail create(@RequestBody @Valid ProductDetail productModel) {
		ProductDetail product = productService.create(productModel);
		return product;
	}

	@DeleteMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public void delete(@PathVariable("id") long id) {
		productService.delete(id);
	}

	@GetMapping()
	public PageResponse<ProductInfoResponse> getPage(
		@ParameterObject PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject GetAllProductFilter filter
	) {
		PageResponse<ProductInfoResponse> productPage = productService.getInPage(filter.getEsCriteria(), paginationAndSortingRequest.getPageable());

		return productPage;
	}
}
