package com.artiexh.api.service.product.impl;

import com.artiexh.api.service.product.JpaProductService;
import com.artiexh.api.service.product.OpenSearchProductService;
import com.artiexh.api.service.product.ProductService;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductSuggestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final OpenSearchProductService openSearchProductService;
	private final JpaProductService jpaProductService;

	@Override
	public Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable) {
		return openSearchProductService.getSuggestionInPage(query, pageable);
	}

	@Override
	public Page<Product> getInPage(Query query, Pageable pageable) {
		var productPage = openSearchProductService.getInPage(query, pageable);
		return jpaProductService.fillProductPage(productPage);
	}

	@Override
	public Product getDetail(long id) {
		return jpaProductService.getDetail(id);
	}

	@Override
	public Product create(long artistId, Product product) {
		Product result;
		try {
			result = jpaProductService.create(artistId, product);
			openSearchProductService.save(result);
		} catch (Exception e) {
			log.warn("Insert product to db fail", e);
			throw e;
		}
		return result;
	}

	@Override
	public Product update(long artistId, Product product) {
		Product result;
		try {
			result = jpaProductService.update(artistId, product);
			openSearchProductService.update(result);
		} catch (Exception e) {
			log.warn("Update product to db fail", e);
			throw e;
		}
		return result;
	}

	@Override
	public void delete(long userId, long productId) {
		try {
			jpaProductService.delete(userId, productId);
			openSearchProductService.delete(productId);
		} catch (Exception e) {
			log.warn("Delete product from db fail", e);
			throw e;
		}

	}
}
