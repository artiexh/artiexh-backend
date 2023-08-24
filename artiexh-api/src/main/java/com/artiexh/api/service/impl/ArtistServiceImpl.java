package com.artiexh.api.service.impl;

import com.artiexh.api.service.ArtistService;
import com.artiexh.api.service.OrderService;
import com.artiexh.api.service.ProductService;
import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.Product;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.artist.ShopOrderResponsePage;
import com.artiexh.model.rest.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ArtistServiceImpl implements ArtistService {
	private final ProductService productService;
	private final OrderService orderService;
	private final ProductMapper productMapper;
	private final OrderMapper orderMapper;

	@Override
	public PageResponse<ProductResponse> getAllProducts(Query query, Pageable pageable) {
		Page<Product> productPage = productService.getInPage(query, pageable);
		return new PageResponse<>(productMapper.domainPageToProductResponsePage(productPage));
	}

	@Override
	public PageResponse<ShopOrderResponsePage> getAllOrder(Specification<OrderEntity> specification, Pageable pageable) {
		Page<Order> orderPage = orderService.getInPage(specification, pageable);
		return new PageResponse<>(orderPage.map(orderMapper::domainToArtistResponsePage));
	}
}
