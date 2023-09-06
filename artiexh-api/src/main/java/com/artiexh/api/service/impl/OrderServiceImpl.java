package com.artiexh.api.service.impl;

import com.artiexh.api.service.OrderService;
import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.data.jpa.repository.ArtistRepository;
import com.artiexh.data.jpa.repository.OrderRepository;
import com.artiexh.data.jpa.repository.UserAddressRepository;
import com.artiexh.ghtk.client.model.GhtkResponse;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeRequest;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.ghtk.client.service.GhtkOrderService;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.mapper.UserAddressMapper;
import com.artiexh.model.mapper.UserMapper;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)

public class OrderServiceImpl implements OrderService {
	private static final Map<OrderStatus, Set<OrderStatus>> orderStatusChangeMap = Map.of(
		OrderStatus.PAYING, Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
		OrderStatus.PREPARING, Set.of(OrderStatus.SHIPPING, OrderStatus.CANCELLED),
		OrderStatus.SHIPPING, Set.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED)
	);

	private final UserAddressRepository userAddressRepository;
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final GhtkOrderService ghtkOrderService;
	private final ArtistRepository artistRepository;
	private final UserMapper userMapper;
	private final UserAddressMapper userAddressMapper;


	@Override
	public Page<Order> getOrderInPage(Specification<OrderEntity> specification, Pageable pageable) {
		Page<OrderEntity> entities = orderRepository.findAll(specification, pageable);
		return entities.map(order -> {
			Order domain = orderMapper.entityToResponseDomain(order);
			domain.setShippingAddress(userAddressMapper.entityToDomain(order.getOrderGroup().getShippingAddress()));
			domain.setUser(userMapper.entityToBasicUser(order.getOrderGroup().getUser()));
			return domain;
		});
	}

	@Override
	public Order getOrderById(Long orderId) {
		OrderEntity order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
		Order domain = orderMapper.entityToResponseDomain(order);
		domain.setShippingAddress(userAddressMapper.entityToDomain(order.getOrderGroup().getShippingAddress()));
		domain.setUser(userMapper.entityToBasicUser(order.getOrderGroup().getUser()));
		return domain;
	}

	// PAYING -> PREPARING -> SHIPPING -> COMPLETED
	//   |           |            |
	// CANCELLED
	@Transactional
	@Override
	public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
		OrderEntity orderEntity = orderRepository.findById(orderId)
			.orElseThrow(EntityNotFoundException::new);

		OrderStatus orderStatus = OrderStatus.fromValue(orderEntity.getStatus());

		if (orderStatusChangeMap.get(orderStatus).contains(newStatus)) {
			orderEntity.setStatus(newStatus.getByteValue());
			orderRepository.save(orderEntity);
			return orderMapper.entityToResponseDomain(orderEntity);
		} else {
			throw new IllegalArgumentException("Cannot change from " + orderStatus + " to " + newStatus);
		}
	}

	@Transactional
	@Override
	public ShipFeeResponse.ShipFee getShippingFee(Long userId, Long addressId, GetShippingFeeRequest getShippingFeeRequest) {
		var addressEntity = userAddressRepository.findByIdAndUserId(addressId, userId)
			.orElseThrow(() -> new IllegalArgumentException("AddressId not belong to user"));

		var shopEntity = artistRepository.findById(getShippingFeeRequest.getShopId())
			.orElseThrow(() -> new IllegalArgumentException("ShopId not existed"));

		var request = ShipFeeRequest.builder()
			.pickAddress(shopEntity.getShopAddress())
			.pickProvince(shopEntity.getShopWard().getDistrict().getProvince().getFullName())
			.pickDistrict(shopEntity.getShopWard().getDistrict().getFullName())
			.pickWard(shopEntity.getShopWard().getFullName())
			.address(addressEntity.getAddress())
			.province(addressEntity.getWard().getDistrict().getProvince().getFullName())
			.district(addressEntity.getWard().getDistrict().getFullName())
			.ward(addressEntity.getWard().getFullName())
			.weight(getShippingFeeRequest.getTotalWeight())
			.deliverOption("none")
			.tags(getShippingFeeRequest.getTags())
			.build();
		var response = ghtkOrderService.getShipFee(request)
			.doOnError(WebClientResponseException.class, throwable -> {
				throw new IllegalArgumentException(
					"Create ghtk order failed: " + throwable.getResponseBodyAs(GhtkResponse.class).getMessage());
			})
			.block();

		return response.getFee();
	}

}
