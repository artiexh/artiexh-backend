package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.exception.RestException;
import com.artiexh.api.service.ProductService;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.common.model.PaginationAndSortingRequest;
import com.artiexh.model.domain.Merch;
import com.artiexh.model.mapper.MerchMapper;
import com.artiexh.model.product.ProductDetail;
import com.artiexh.model.product.ProductInfo;
import com.artiexh.model.product.request.GetAllProductFilter;
import com.artiexh.model.product.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path ="/test" + Endpoint.Product.ROOT)
public class ProductController {
	private final ProductService productService;

	@GetMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public ProductDetail getDetail(@PathVariable("id") long id) {
		try {
			ProductDetail product = productService.getDetail(id);
			return product;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PostMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public ProductDetail update(
		@PathVariable("id") long id,
		@RequestBody ProductDetail request) {
		try {
			request.setId(id);
			ProductDetail product = productService.update(request);
			return product;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PostMapping
	public ProductDetail create(@RequestBody ProductDetail productModel) {
		try {
			ProductDetail product = productService.create(productModel);
			return product;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@DeleteMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public void delete(@PathVariable("id") long id) {
		try {
			productService.delete(id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping(path = Endpoint.Product.PRODUCT_PAGE)
	public PageResponse<ProductInfo> getPage(
		@ParameterObject PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject GetAllProductFilter filter
		) {
		try {
			PageResponse<ProductInfo> productPage = productService.getInPage(filter.getSpecification(), paginationAndSortingRequest.getPageable());

			return productPage;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping(path = Endpoint.Product.PRODUCT_LIST)
	public List<ProductInfo> getList(@ParameterObject GetAllProductFilter filter) {
		try {
			List<ProductInfo> productList = productService.getInList(filter.getSpecification());
			return productList;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
