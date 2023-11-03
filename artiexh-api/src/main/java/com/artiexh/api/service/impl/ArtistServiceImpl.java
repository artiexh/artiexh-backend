package com.artiexh.api.service.impl;

import com.artiexh.api.service.ArtistService;
import com.artiexh.api.service.OrderService;
import com.artiexh.api.service.PostService;
import com.artiexh.api.service.product.ProductService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.ArtistRepository;
import com.artiexh.data.jpa.repository.OrderHistoryRepository;
import com.artiexh.data.jpa.repository.OrderRepository;
import com.artiexh.data.jpa.repository.SubscriptionRepository;
import com.artiexh.ghtk.client.model.GhtkResponse;
import com.artiexh.ghtk.client.model.order.CreateOrderRequest;
import com.artiexh.ghtk.client.service.GhtkOrderService;
import com.artiexh.model.domain.*;
import com.artiexh.model.mapper.ArtistMapper;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.mapper.ProductMapper;
import com.artiexh.model.mapper.SubscriptionMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.artist.request.UpdateArtistProfileRequest;
import com.artiexh.model.rest.artist.response.ArtistProfileResponse;
import com.artiexh.model.rest.artist.response.ShopOrderResponse;
import com.artiexh.model.rest.artist.response.ShopOrderResponsePage;
import com.artiexh.model.rest.order.request.UpdateShippingOrderRequest;
import com.artiexh.model.rest.product.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ArtistServiceImpl implements ArtistService {

	private final ArtistRepository artistRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final OrderRepository orderRepository;
	private final OrderHistoryRepository orderHistoryRepository;
	private final ProductService productService;
	private final OrderService orderService;
	private final GhtkOrderService ghtkOrderService;
	private final PostService postService;
	private final ProductMapper productMapper;
	private final OrderMapper orderMapper;
	private final ArtistMapper artistMapper;
	private final SubscriptionMapper subscriptionMapper;

	@Override
	public ArtistProfileResponse getProfile(long id) {
		ArtistEntity artistEntity = artistRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Artist id: " + id + " not found"));
		return getProfile(artistEntity);
	}

	@Override
	public ArtistProfileResponse getProfile(String username) {
		ArtistEntity artistEntity = artistRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException("Artist: " + username + " not found"));
		return getProfile(artistEntity);
	}

	private ArtistProfileResponse getProfile(ArtistEntity artistEntity) {
		ArtistProfileResponse result = artistMapper.entityToProfileResponse(artistEntity);

		Pageable pageable = PageRequest.of(1, 10);
		Page<SubscriptionEntity> subscriptionPage = subscriptionRepository.findAllByArtistId(artistEntity.getId(), pageable);

		result.setNumOfSubscriptions(subscriptionPage.getTotalElements());
		result.setSubscriptionsFrom(subscriptionPage.map(subscriptionMapper::subscriptionEntityToArtistSubscription).toSet());

		return result;
	}

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

		var products = orderEntity.getOrderDetails().stream().map(orderDetailEntity -> {
			var productEntity = orderDetailEntity.getProduct();
			return com.artiexh.ghtk.client.model.order.Product.builder().name(productEntity.getName())
				.weight(Double.valueOf(productEntity.getWeight())).productCode("").build();
		}).collect(Collectors.toSet());

		var orderBuilder = CreateOrderRequest.Order.builder()
			.id(orderEntity.getId().toString())
			.pickMoney(0) // no cod
			.name(orderEntity.getOrderGroup().getShippingAddress().getReceiverName())
			.address(orderEntity.getOrderGroup().getDeliveryAddress())
			.province(orderEntity.getOrderGroup().getDeliveryProvince())
			.district(orderEntity.getOrderGroup().getDeliveryDistrict())
			.ward(orderEntity.getOrderGroup().getDeliveryWard())
			.hamlet("Khác")
			.tel(orderEntity.getOrderGroup().getDeliveryTel())
			.email(orderEntity.getOrderGroup().getDeliveryEmail())
			.note(updateShippingOrderRequest.getNote())
			.weightOption("gram")
			.tags(updateShippingOrderRequest.getTags());

		if (updateShippingOrderRequest.getValue() != null) {
			orderBuilder.value(updateShippingOrderRequest.getValue().intValue());
		} else {
			var value = orderEntity.getOrderDetails().stream()
				.map(orderDetailEntity -> {
					var productEntity = orderDetailEntity.getProduct();
					return productEntity.getPriceAmount().multiply(BigDecimal.valueOf(orderDetailEntity.getQuantity()));
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);
			orderBuilder.value(value.intValue());
		}

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
			orderEntity.setPickName(updateShippingOrderRequest.getPickName());
			orderEntity.setPickAddress(updateShippingOrderRequest.getPickAddress());
			orderEntity.setPickProvince(updateShippingOrderRequest.getPickProvince());
			orderEntity.setPickDistrict(updateShippingOrderRequest.getPickDistrict());
			orderEntity.setPickWard(updateShippingOrderRequest.getPickWard());
			orderEntity.setPickTel(updateShippingOrderRequest.getPickTel());
			orderEntity.setPickEmail(updateShippingOrderRequest.getPickEmail());
		} else {
			orderBuilder
				.pickName(orderEntity.getShop().getDisplayName()) // artist display name
				.pickAddress(orderEntity.getShop().getAddress())
				.pickProvince(orderEntity.getShop().getShopWard().getDistrict().getProvince().getFullName())
				.pickDistrict(orderEntity.getShop().getShopWard().getDistrict().getFullName())
				.pickWard(orderEntity.getShop().getShopWard().getFullName())
				.pickTel(orderEntity.getShop().getPhone())
				.pickTel(orderEntity.getShop().getPhone())
				.pickEmail(orderEntity.getShop().getEmail());
			orderEntity.setPickName(orderEntity.getShop().getDisplayName());
			orderEntity.setPickAddress(orderEntity.getShop().getAddress());
			orderEntity.setPickProvince(orderEntity.getShop().getShopWard().getDistrict().getProvince().getFullName());
			orderEntity.setPickDistrict(orderEntity.getShop().getShopWard().getDistrict().getFullName());
			orderEntity.setPickWard(orderEntity.getShop().getShopWard().getFullName());
			orderEntity.setPickTel(orderEntity.getShop().getPhone());
			orderEntity.setPickEmail(orderEntity.getShop().getEmail());
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
			orderEntity.setReturnName(updateShippingOrderRequest.getReturnName());
			orderEntity.setReturnAddress(updateShippingOrderRequest.getReturnAddress());
			orderEntity.setReturnProvince(updateShippingOrderRequest.getReturnProvince());
			orderEntity.setReturnDistrict(updateShippingOrderRequest.getReturnDistrict());
			orderEntity.setReturnWard(updateShippingOrderRequest.getReturnWard());
			orderEntity.setReturnTel(updateShippingOrderRequest.getReturnTel());
			orderEntity.setReturnEmail(updateShippingOrderRequest.getReturnEmail());
		} else {
			orderEntity.setReturnName(orderEntity.getPickName());
			orderEntity.setReturnAddress(orderEntity.getPickAddress());
			orderEntity.setReturnProvince(orderEntity.getPickProvince());
			orderEntity.setReturnDistrict(orderEntity.getPickDistrict());
			orderEntity.setReturnWard(orderEntity.getPickWard());
			orderEntity.setReturnTel(orderEntity.getPickTel());
			orderEntity.setReturnEmail(orderEntity.getPickEmail());
		}

		var request = CreateOrderRequest.builder().order(orderBuilder.build()).products(products).build();
		var ghtkCreateOrderResponse = ghtkOrderService.createOrder(request, "1.5")
			.doOnError(WebClientResponseException.class, throwable -> {
				var response = throwable.getResponseBodyAs(GhtkResponse.class);
				throw new IllegalArgumentException(
					"Create ghtk order failed: " + ((response != null && response.getMessage() != null) ? response.getMessage() : "Unknown response"));
			})
			.block();

		if (ghtkCreateOrderResponse == null) {
			throw new IllegalArgumentException("Create ghtk order failed: Unknown response");
		} else if (ghtkCreateOrderResponse.getOrder() == null) {
			throw new IllegalArgumentException("Create ghtk order failed: " + ghtkCreateOrderResponse.getMessage());
		}

		orderEntity.setStatus(OrderStatus.SHIPPING.getByteValue());
		orderEntity.setShippingLabel(ghtkCreateOrderResponse.getOrder().getLabel());
		orderRepository.save(orderEntity);

		var orderHistoryEntity = OrderHistoryEntity.builder()
			.id(new OrderHistoryEntityId(orderId, OrderHistoryStatus.SHIPPED.getByteValue()))
			.build();
		var savedOrderHistoryEntity = orderHistoryRepository.save(orderHistoryEntity);

		orderEntity.getOrderHistories().add(savedOrderHistoryEntity);
		return orderMapper.domainToArtistResponse(orderMapper.entityToResponseDomain(orderEntity));
	}

	@Override
	public Page<Post> getArtistPost(Long artistId, Pageable pageable) {
		if (!artistRepository.existsById(artistId)) {
			throw new EntityNotFoundException("Arist id: " + artistId + " not existed");
		}
		return postService.getAllPost(artistId, pageable);
	}

	@Override
	@Transactional
	public ArtistProfileResponse updateArtistProfile(Long artistId, UpdateArtistProfileRequest request) {
		ArtistEntity entity = artistRepository.findById(artistId).orElseThrow(EntityNotFoundException::new);

		entity.setBankAccount(request.getBankAccount());
		entity.setBankName(request.getBankName());
		entity.setAddress(request.getAddress());
		entity.setShopWard(WardEntity.builder().id(request.getWardId()).build());
		entity.setPhone(request.getPhone());

		artistRepository.save(entity);

		ArtistProfileResponse profile = artistMapper.entityToProfileResponse(entity);
		profile.setNumOfSubscriptions(subscriptionRepository.countByUserId(entity.getId()));
		return profile;
	}
}
