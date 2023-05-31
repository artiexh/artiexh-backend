package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.RestException;
import com.artiexh.api.service.ProductService;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.common.model.PaginationAndSortingRequest;
import com.artiexh.model.domain.Merch;
import com.artiexh.model.mapper.MerchMapper;
import com.artiexh.model.mapper.UserMapper;
import com.artiexh.model.request.product.GetAllProductFilter;
import com.artiexh.model.request.product.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path ="/test" + Endpoint.Product.ROOT)
public class ProductController {
	private final MerchMapper merchMapper;
	private final ProductService productService;

	@GetMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public ResponseEntity<Merch> getDetail(@PathVariable("id") long id) {
		try {
			Merch product = productService.getDetail(id);
			return ResponseEntity.ok(product);
		} catch (Exception e) {
			throw new RestException();
		}
	}

	@PostMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public ResponseEntity<Merch> update(
		@PathVariable("id") long id,
		@RequestBody UpdateProductRequest request) {
		try {
			request.setId(id);
			Merch productModel = merchMapper.requestToDomainModel(request);
			Merch product = productService.update(productModel);
			return ResponseEntity.ok(product);
		} catch (Exception e) {
			throw new RestException();
		}
	}

	@PostMapping
	public ResponseEntity<Merch> create(@RequestBody Merch productModel) {
		try {
			Merch product = productService.create(productModel);
			return ResponseEntity.ok(product);
		} catch (Exception e) {
			throw new RestException();
		}
	}

	@DeleteMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public ResponseEntity<Void> delete(@PathVariable("id") long id) {
		try {
			productService.delete(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			throw new RestException();
		}
	}

	@GetMapping(path = Endpoint.Product.PRODUCT_PAGE)
	public ResponseEntity<PageResponse<Merch>> getPage(
		@ParameterObject PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject GetAllProductFilter filter
		) {
		try {
			PageResponse<Merch> productPage = productService.getInPage(filter.getSpecification(), paginationAndSortingRequest.getPageable());
			return ResponseEntity.ok(productPage);
		} catch (Exception e) {
			throw new RestException();
		}
	}

	@GetMapping(path = Endpoint.Product.PRODUCT_LIST)
	public ResponseEntity<List<Merch>> getList(@ParameterObject GetAllProductFilter filter) {
		try {
			List<Merch> productList = productService.getInList(filter.getSpecification());
			return ResponseEntity.ok(productList);
		} catch (Exception e) {
			throw new RestException();
		}
	}
}
