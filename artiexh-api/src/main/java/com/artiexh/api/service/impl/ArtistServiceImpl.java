package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.ArtistService;
import com.artiexh.api.service.OrderService;
import com.artiexh.api.service.ProductService;
import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.data.jpa.repository.OrderRepository;
import com.artiexh.ghtk.client.model.GhtkResponse;
import com.artiexh.ghtk.client.model.order.CreateOrderRequest;
import com.artiexh.ghtk.client.service.GhtkOrderService;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.domain.Product;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.artist.ShopOrderResponse;
import com.artiexh.model.rest.artist.ShopOrderResponsePage;
import com.artiexh.model.rest.order.request.UpdateShippingOrderRequest;
import com.artiexh.model.rest.product.response.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArtistServiceImpl implements ArtistService {

	private final OrderRepository orderRepository;
	private final ProductService productService;
	private final OrderService orderService;
	private final ProductMapper productMapper;
	private final OrderMapper orderMapper;
	private final GhtkOrderService ghtkOrderService;
	private final ObjectMapper jacksonObjectMapper;

	@Override
	public PageResponse<ProductResponse> getAllProducts(Query query, Pageable pageable) {
		Page<Product> productPage = productService.getInPage(query, pageable);
		return new PageResponse<>(productMapper.productPageToProductResponsePage(productPage));
	}

	@Override
	public ShopOrderResponse getOrderById(Long orderId, Long artistId) {
		Order order = orderService.getById(orderId);
		if (!order.getShop().getId().equals(artistId)) {
			throw new IllegalArgumentException(ErrorCode.ORDER_IS_INVALID.getMessage());
		}
		return orderMapper.orderToArtistResponse(order);
	}

	@Override
	public PageResponse<ShopOrderResponsePage> getAllOrder(Specification<OrderEntity> specification,
														   Pageable pageable) {
		Page<Order> orderPage = orderService.getInPage(specification, pageable);
		return new PageResponse<>(orderPage.map(orderMapper::orderToArtistResponse));
	}

	@Transactional
	@Override
	public ShopOrderResponse updateShippingOrderStatus(Long artistId, Long orderId,
													   UpdateShippingOrderRequest updateShippingOrderRequest) {
		OrderEntity orderEntity = orderRepository.findByIdAndShopId(orderId, artistId).orElseThrow(
			() -> new IllegalArgumentException("OrderId " + orderId + " is not belongs to artist " + artistId));

		if (orderEntity.getStatus() != OrderStatus.PREPARING.getByteValue()) {
			throw new IllegalArgumentException(
				"Cannot update order status from " + OrderStatus.fromValue(orderEntity.getStatus()) + " to SHIPPING");
		}

		if (updateShippingOrderRequest.getValue() == null
			|| updateShippingOrderRequest.getValue().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Value must be greater than 0");
		}

		var products = orderEntity.getOrderDetails().stream().map(orderDetailEntity -> {
			var productEntity = orderDetailEntity.getProduct();
			return com.artiexh.ghtk.client.model.order.Product.builder().name(productEntity.getName())
				.weight(Double.valueOf(productEntity.getWeight())).productCode("").build();
		}).collect(Collectors.toSet());

		var order = CreateOrderRequest.Order.builder().id(orderEntity.getId().toString())
			.pickName(orderEntity.getShop().getDisplayName()) // artist display name
			.pickMoney(0) // no cod
			.pickAddress(orderEntity.getShop().getShopAddress())
			.pickProvince(orderEntity.getShop().getShopWard().getDistrict().getProvince().getFullName())
			.pickDistrict(orderEntity.getShop().getShopWard().getDistrict().getFullName())
			.pickWard(orderEntity.getShop().getShopWard().getFullName()).pickTel(orderEntity.getShop().getShopPhone())
			.pickTel(orderEntity.getShop().getShopPhone())
			.name(orderEntity.getShippingAddress().getReceiverName())
			.address(orderEntity.getShippingAddress().getAddress())
			.province(orderEntity.getShippingAddress().getWard().getDistrict().getProvince().getFullName())
			.district(orderEntity.getShippingAddress().getWard().getDistrict().getFullName())
			.ward(orderEntity.getShippingAddress().getWard().getFullName()).hamlet("Khác")
			.tel(orderEntity.getShippingAddress().getPhone()).email(orderEntity.getUser().getEmail())
			.weightOption("gram")
			.value(updateShippingOrderRequest.getValue().intValue()).build();

		var request = CreateOrderRequest.builder().order(order).products(products).build();
		ghtkOrderService.createOrder(request, "1.5")
			.doOnError(WebClientResponseException.class, throwable -> {
				throw new IllegalArgumentException(
					"Create ghtk order failed: " + throwable.getResponseBodyAs(GhtkResponse.class).getMessage());
			})
			.block();

		orderEntity.setStatus(OrderStatus.SHIPPING.getByteValue());
		orderRepository.save(orderEntity);
		return orderMapper.orderToArtistResponse(orderMapper.entityToResponseDomain(orderEntity));
	}
}
