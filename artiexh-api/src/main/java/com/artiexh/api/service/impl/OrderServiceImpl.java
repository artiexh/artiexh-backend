package com.artiexh.api.service.impl;

import com.artiexh.api.service.OrderService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.OrderRepository;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.data.jpa.repository.UserAddressRepository;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.domain.PaymentMethod;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.rest.order.request.CheckoutItem;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import com.artiexh.model.rest.order.request.CheckoutShop;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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


	@Override
	public List<Order> checkout(long userId, CheckoutRequest checkoutRequest) {
		List<OrderEntity> orderEntities = switch (checkoutRequest.getPaymentMethod()) {
			case CASH -> cashPaymentOrder(userId, checkoutRequest);
			default -> onlinePaymentOrder(userId, checkoutRequest);
		};

		return orderEntities.stream().map(orderMapper::entityToResponseDomain).collect(Collectors.toList());
	}

	private List<OrderEntity> cashPaymentOrder(long userId, CheckoutRequest checkoutRequest) {
		// check common data
		UserAddressEntity address = getUserAddressEntity(userId, checkoutRequest.getAddressId());
		OrderStatus status = OrderStatus.PREPARING;

		// check and reduce product quantity
		validateShopProductAndReduceQuantity(checkoutRequest.getShops(), checkoutRequest.getPaymentMethod());

		// create order and order details
		try {
			return createOrder(userId, address, status, checkoutRequest.getShops(), checkoutRequest.getPaymentMethod());
		} catch (Exception ex) {
			rollbackShopProductQuantity(checkoutRequest.getShops());
			throw ex;
		}
	}

	private List<OrderEntity> onlinePaymentOrder(long userId, CheckoutRequest checkoutRequest) {
		// check common data
		UserAddressEntity address = getUserAddressEntity(userId, checkoutRequest.getAddressId());
		OrderStatus status = OrderStatus.PAYING;

		validateShopProductAndReduceQuantity(checkoutRequest.getShops(), checkoutRequest.getPaymentMethod());

		// create order and order details
		try {
			return createOrder(userId, address, status, checkoutRequest.getShops(), checkoutRequest.getPaymentMethod());
		} catch (Exception ex) {
			rollbackShopProductQuantity(checkoutRequest.getShops());
			throw ex;
		}
	}

	@Transactional
	public UserAddressEntity getUserAddressEntity(long userId, long addressId) {
		return userAddressRepository.findByIdAndUserId(addressId, userId)
			.orElseThrow(() -> new IllegalArgumentException("Address not existed"));
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void validateShopProductAndReduceQuantity(Set<CheckoutShop> shops, PaymentMethod paymentMethod) {
		for (var checkoutShop : shops) {
			// product_id, shop_id, product_status
			Set<ProductEntity> productEntities = productRepository.findAllByIdInAndShopIdAndStatus(
				checkoutShop.getItems().stream().map(CheckoutItem::getProductId).collect(Collectors.toSet()),
				checkoutShop.getShopId(),
				ProductStatus.AVAILABLE.getByteValue()
			);
			if (productEntities.size() != checkoutShop.getItems().size()) {
				throw new IllegalArgumentException("Not valid shopIds or productIds");
			}

			// product_payment_method
			for (var productEntity : productEntities) {
				Set<Byte> acceptedPaymentMethods = Set.of(productEntity.getPaymentMethods());
				if (!acceptedPaymentMethods.contains(paymentMethod.getByteValue())) {
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

			productRepository.saveAll(productEntities);
		}
	}

	@Transactional
	public List<OrderEntity> createOrder(long userId, UserAddressEntity address, OrderStatus status,
										 Set<CheckoutShop> shops, PaymentMethod paymentMethod) {
		Set<OrderEntity> orderEntities = shops.stream().map(checkoutShop ->
				OrderEntity.builder()
					.user(UserEntity.builder().id(userId).build())
					.shop(ArtistEntity.builder().id(checkoutShop.getShopId()).build())
					.shippingAddress(address)
					.note(checkoutShop.getNote())
					.paymentMethod(paymentMethod.getByteValue())
					.status(status.getByteValue())
					.orderDetails(checkoutShop.getItems().stream()
						.map(checkoutItem -> OrderDetailEntity.builder()
							.product(ProductEntity.builder().id(checkoutItem.getProductId()).build())
							.quantity(checkoutItem.getQuantity())
							.build()
						)
						.collect(Collectors.toSet())
					)
					.build()
			)
			.collect(Collectors.toSet());
		return orderRepository.saveAll(orderEntities);
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void rollbackShopProductQuantity(Set<CheckoutShop> shops) {
		for (var checkoutShop : shops) {
			Set<ProductEntity> productEntities = productRepository.findAllByIdIn(checkoutShop.getItems().stream()
				.map(CheckoutItem::getProductId)
				.collect(Collectors.toSet())
			);

			for (var productEntity : productEntities) {
				for (var checkoutItem : checkoutShop.getItems()) {
					if (productEntity.getId().equals(checkoutItem.getProductId())) {
						productEntity.setRemainingQuantity(productEntity.getRemainingQuantity() + checkoutItem.getQuantity());
					}
				}
			}

			productRepository.saveAll(productEntities);
		}
	}

	@Override
	public Page<Order> getInPage(Specification<OrderEntity> specification, Pageable pageable) {
		Page<OrderEntity> entities = orderRepository.findAll(specification, pageable);
		Page<Order> orderPage = entities.map(orderMapper::entityToResponseDomain);
		return orderPage;
	}

	@Override
	public Order getById(Long orderId) {
		OrderEntity entity = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
		return orderMapper.entityToResponseDomain(entity);
	}

}
