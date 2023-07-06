package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ProductService;
import com.artiexh.model.domain.Product;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.product.request.CreateProductRequest;
import com.artiexh.model.rest.product.request.GetAllProductFilter;
import com.artiexh.model.rest.product.request.UpdateProductRequest;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.Product.ROOT)
public class ProductController {
	private final ProductService productService;
	private final ProductMapper productMapper;

	@GetMapping
	public PageResponse<ProductResponse> getInPage(
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject @Valid GetAllProductFilter filter
	) {
		Page<Product> productPage = productService.getInPage(
			filter.getQuery(),
			paginationAndSortingRequest.getPageable()
		);
		return new PageResponse<>(productMapper.domainPageToProductResponsePage(productPage));
	}

	@GetMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public ProductResponse getDetail(@PathVariable("id") long id) {
		Product product;
		try {
			product = productService.getDetail(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(ErrorCode.PRODUCT_NOT_FOUND.getCode(), ErrorCode.PRODUCT_NOT_FOUND.getMessage(), ex);
		}
		return productMapper.domainToProductResponse(product);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('ARTIST')")
	public ProductResponse create(Authentication authentication, @RequestBody @Valid CreateProductRequest productRequest) {
		long userId = (long) authentication.getPrincipal();
		try {
			Product createdProduct = productService.create(userId, productMapper.createProductRequestToProduct(productRequest));
			return productMapper.domainToProductResponse(createdProduct);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@PutMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	@PreAuthorize("hasAuthority('ARTIST')")
	public ProductResponse update(Authentication authentication, @PathVariable("id") long id, @RequestBody @Valid UpdateProductRequest request) {
		long userId = (long) authentication.getPrincipal();

		Product product = productMapper.updateProductRequestToProduct(request);
		product.setId(id);
		try {
			Product updatedProduct = productService.update(userId, product);
			return productMapper.domainToProductResponse(updatedProduct);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(ErrorCode.PRODUCT_NOT_FOUND.getCode(), ErrorCode.PRODUCT_NOT_FOUND.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@DeleteMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	public void delete(Authentication authentication, @PathVariable("id") long id) {
		Long userId = (Long) authentication.getPrincipal();
		try {
			productService.delete(userId, id);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, ex.getMessage(), ex);
		}
	}
}
