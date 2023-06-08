package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.ProductService;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.common.model.PaginationAndSortingRequest;
import com.artiexh.model.product.ProductDetail;
import com.artiexh.model.product.ProductInfo;
import com.artiexh.model.product.request.GetAllProductFilter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	@PostMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public ProductDetail update(
		@PathVariable("id") long id,
		@RequestBody ProductDetail request) {
		request.setId(id);
		ProductDetail product = productService.update(request);
		return product;
	}

	@PostMapping
	public ProductDetail create(@RequestBody ProductDetail productModel) {
		ProductDetail product = productService.create(productModel);
		return product;
	}

	@DeleteMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public void delete(@PathVariable("id") long id) {
		productService.delete(id);
	}

	@GetMapping(path = Endpoint.Product.PRODUCT_PAGE)
	public PageResponse<ProductInfo> getPage(
		@ParameterObject PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject GetAllProductFilter filter
	) {
		PageResponse<ProductInfo> productPage = productService.getInPage(filter.getSpecification(), paginationAndSortingRequest.getPageable());

		return productPage;
	}

	@GetMapping(path = Endpoint.Product.PRODUCT_LIST)
	public List<ProductInfo> getList(@ParameterObject GetAllProductFilter filter) {
		List<ProductInfo> productList = productService.getInList(filter.getSpecification());
		return productList;
	}
}
