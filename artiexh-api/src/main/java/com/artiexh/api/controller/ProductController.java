package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.service.product.ProductService;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductSuggestion;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.product.request.CreateProductRequest;
import com.artiexh.model.rest.product.request.GetAllProductFilter;
import com.artiexh.model.rest.product.request.SuggestionFilter;
import com.artiexh.model.rest.product.request.UpdateProductRequest;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.MatchAllQueryBuilder;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.Query;
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

	@PutMapping(path = Endpoint.Product.PRODUCT_DETAIL)
	@PreAuthorize("hasAnyAuthority('ARTIST','ADMIN')")
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

	@GetMapping()
	@PreAuthorize("hasAnyAuthority('STAFF', 'ADMIN', 'ARTIST')")
	public PageResponse<ProductResponse> getInPage(
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest
	) {
		GetAllProductFilter filter = new GetAllProductFilter();
		Page<Product> productPage = productService.getInPage(
			filter.matchAllQuery(),
			paginationAndSortingRequest.getPageable()
		);
		return new PageResponse<>(productMapper.productPageToProductResponsePage(productPage));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('STAFF', 'ADMIN', 'ARTIST')")
	public ProductResponse getDetail(@PathVariable("id") long id) {
		Product product;
		try {
			product = productService.getDetail(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(ErrorCode.PRODUCT_NOT_FOUND.getCode(), ErrorCode.PRODUCT_NOT_FOUND.getMessage(), ex);
		}
		return productMapper.domainToProductResponse(product);
	}
}
