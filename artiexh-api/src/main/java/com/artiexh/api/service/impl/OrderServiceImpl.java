package com.artiexh.api.service.impl;

import com.artiexh.api.service.OrderService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.OrderRepository;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.data.jpa.repository.UserAddressRepository;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.rest.order.request.CheckoutItem;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
	private final UserAddressRepository userAddressRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;


	@Transactional
	@Override
	public void checkout(long userId, CheckoutRequest checkoutRequest) {
		switch (checkoutRequest.getPaymentMethod()) {
			case CASH -> cashPaymentOrder(userId, checkoutRequest);
			default -> onlinePaymentOrder(userId, checkoutRequest);
		}
	}

	private void cashPaymentOrder(long userId, CheckoutRequest checkoutRequest) {
		UserAddressEntity address = userAddressRepository.findByIdAndUserId(userId, checkoutRequest.getAddressId())
			.orElseThrow(() -> new IllegalArgumentException("Address not existed"));

		OrderStatus status = OrderStatus.PREPARING;

		for (var checkoutShop : checkoutRequest.getShops()) {
			Set<ProductEntity> productEntities = productRepository.findAllByIdInAndShopIdAndStatus(
				checkoutShop.getItems().stream().map(CheckoutItem::getProductId).collect(Collectors.toSet()),
				checkoutShop.getShopId(),
				ProductStatus.AVAILABLE.getByteValue()
			);

			if (productEntities.size() != checkoutShop.getItems().size()) {
				throw new IllegalArgumentException("Not valid product ids");
			}

			for (var productEntity : productEntities) {
				Set<Byte> acceptedPaymentMethods = Set.of(productEntity.getPaymentMethods());
				if (!acceptedPaymentMethods.contains(checkoutRequest.getPaymentMethod().getByteValue())) {
					throw new IllegalArgumentException("Not accepted payment method for product id: " + productEntity.getId());
				}
			}

			for (var productEntity : productEntities) {
				for (var checkoutItem : checkoutShop.getItems()) {
					if (productEntity.getId().equals(checkoutItem.getProductId())) {
						if (productEntity.getRemainingQuantity() < checkoutItem.getQuantity()) {
							throw new IllegalArgumentException("Not enough quantity for product id: " + productEntity.getId());
						} else {
							productEntity.setRemainingQuantity(productEntity.getRemainingQuantity() - checkoutItem.getQuantity());
						}
					}
				}
			}

			try {
				productRepository.saveAll(productEntities);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Not enough quantity", ex);
			}

			OrderEntity orderEntity = OrderEntity.builder()
				.user(UserEntity.builder().id(userId).build())
				.shop(ArtistEntity.builder().id(checkoutShop.getShopId()).build())
				.shippingAddress(address)
				.note(checkoutShop.getNote())
				.paymentMethod(checkoutRequest.getPaymentMethod().getByteValue())
				.status(status.getByteValue())
				.orderDetails(checkoutShop.getItems().stream()
					.map(checkoutItem -> OrderDetailEntity.builder()
						.product(ProductEntity.builder().id(checkoutItem.getProductId()).build())
						.quantity(checkoutItem.getQuantity())
						.build()
					)
					.collect(Collectors.toSet())
				)
				.modifiedDate(LocalDateTime.now())
				.createdDate(LocalDateTime.now())
				.build();

		}
	}

	private void onlinePaymentOrder(long userId, CheckoutRequest checkoutRequest) {
		UserAddressEntity address = userAddressRepository.findByIdAndUserId(userId, checkoutRequest.getAddressId())
			.orElseThrow(() -> new IllegalArgumentException("Address not existed"));
		OrderStatus status = OrderStatus.PAYING;
	}

	@Override
	public Page<Order> getInPage(Specification<OrderEntity> specification, Pageable pageable) {
		Page<OrderEntity> entities = orderRepository.findAll(specification, pageable);
		Page<Order> orderPage = entities.map(orderMapper::entityToDomain);
		return orderPage;
	}

}
