package com.artiexh.api.service.impl;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

	@Override
	public PageResponse<ProductResponse> getAllProducts(Query query, Pageable pageable) {
		Page<Product> productPage = productService.getInPage(query, pageable);
		return new PageResponse<>(productMapper.productPageToProductResponsePage(productPage));
	}

	@Override
	public ShopOrderResponse getOrderById(Long orderId, Long artistId) {
		Order order = orderService.getOrderByIdAndShopId(orderId, artistId);
		return orderMapper.domainToArtistResponse(order);
	}

	@Override
	public PageResponse<ShopOrderResponsePage> getAllOrder(Specification<OrderEntity> specification,
														   Pageable pageable) {
		Page<Order> orderPage = orderService.getOrderInPage(specification, pageable);
		return new PageResponse<>(orderPage.map(orderMapper::domainToArtistResponsePage));
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

		var orderBuilder = CreateOrderRequest.Order.builder().id(orderEntity.getId().toString())
			.pickMoney(0) // no cod
			.name(orderEntity.getOrderGroup().getShippingAddress().getReceiverName())
			.address(orderEntity.getOrderGroup().getShippingAddress().getAddress())
			.province(orderEntity.getOrderGroup().getShippingAddress().getWard().getDistrict().getProvince().getFullName())
			.district(orderEntity.getOrderGroup().getShippingAddress().getWard().getDistrict().getFullName())
			.ward(orderEntity.getOrderGroup().getShippingAddress().getWard().getFullName()).hamlet("Khác")
			.tel(orderEntity.getOrderGroup().getShippingAddress().getPhone()).email(orderEntity.getOrderGroup().getUser().getEmail())
			.note(updateShippingOrderRequest.getNote())
			.weightOption("gram")
			.value(updateShippingOrderRequest.getValue().intValue())
			.tags(updateShippingOrderRequest.getTags());

		if (updateShippingOrderRequest.getPickWorkShift() != null) {
			orderBuilder.pickWorkShift(updateShippingOrderRequest.getPickWorkShift().getValue());
		}

		if (updateShippingOrderRequest.getDeliverWorkShift() != null) {
			orderBuilder.deliverWorkShift(updateShippingOrderRequest.getDeliverWorkShift().getValue());
		}

		if (StringUtils.hasText(updateShippingOrderRequest.getPickAddress()) &&
			StringUtils.hasText(updateShippingOrderRequest.getPickProvince()) &&
			StringUtils.hasText(updateShippingOrderRequest.getPickDistrict()) &&
			StringUtils.hasText(updateShippingOrderRequest.getPickWard()) &&
			StringUtils.hasText(updateShippingOrderRequest.getPickName()) &&
			StringUtils.hasText(updateShippingOrderRequest.getPickTel()) &&
			StringUtils.hasText(updateShippingOrderRequest.getPickEmail())) {
			orderBuilder
				.pickName(updateShippingOrderRequest.getPickName())
				.pickAddress(updateShippingOrderRequest.getPickAddress())
				.pickProvince(updateShippingOrderRequest.getPickProvince())
				.pickDistrict(updateShippingOrderRequest.getPickDistrict())
				.pickWard(updateShippingOrderRequest.getPickWard())
				.pickTel(updateShippingOrderRequest.getPickTel())
				.pickEmail(updateShippingOrderRequest.getPickEmail());
		} else {
			orderBuilder
				.pickName(orderEntity.getShop().getDisplayName()) // artist display name
				.pickAddress(orderEntity.getShop().getShopAddress())
				.pickProvince(orderEntity.getShop().getShopWard().getDistrict().getProvince().getFullName())
				.pickDistrict(orderEntity.getShop().getShopWard().getDistrict().getFullName())
				.pickWard(orderEntity.getShop().getShopWard().getFullName()).pickTel(orderEntity.getShop().getShopPhone())
				.pickTel(orderEntity.getShop().getShopPhone())
				.pickEmail(orderEntity.getShop().getEmail());
		}

		if (Integer.valueOf(1).equals(updateShippingOrderRequest.getUseReturnAddress()) &&
			StringUtils.hasText(updateShippingOrderRequest.getReturnName()) &&
			StringUtils.hasText(updateShippingOrderRequest.getReturnAddress()) &&
			StringUtils.hasText(updateShippingOrderRequest.getReturnProvince()) &&
			StringUtils.hasText(updateShippingOrderRequest.getReturnDistrict()) &&
			StringUtils.hasText(updateShippingOrderRequest.getReturnWard()) &&
			StringUtils.hasText(updateShippingOrderRequest.getReturnTel()) &&
			StringUtils.hasText(updateShippingOrderRequest.getReturnEmail())) {
			orderBuilder
				.useReturnAddress(updateShippingOrderRequest.getUseReturnAddress())
				.returnName(updateShippingOrderRequest.getReturnName())
				.returnAddress(updateShippingOrderRequest.getReturnAddress())
				.returnProvince(updateShippingOrderRequest.getReturnProvince())
				.returnDistrict(updateShippingOrderRequest.getReturnDistrict())
				.returnWard(updateShippingOrderRequest.getReturnWard())
				.returnTel(updateShippingOrderRequest.getReturnTel())
				.returnEmail(updateShippingOrderRequest.getReturnEmail());
		}

		var request = CreateOrderRequest.builder().order(orderBuilder.build()).products(products).build();
		ghtkOrderService.createOrder(request, "1.5")
			.doOnError(WebClientResponseException.class, throwable -> {
				throw new IllegalArgumentException(
					"Create ghtk order failed: " + throwable.getResponseBodyAs(GhtkResponse.class).getMessage());
			})
			.block();

		orderEntity.setStatus(OrderStatus.SHIPPING.getByteValue());
		orderRepository.save(orderEntity);
		return orderMapper.domainToArtistResponse(orderMapper.entityToResponseDomain(orderEntity));
	}
}
