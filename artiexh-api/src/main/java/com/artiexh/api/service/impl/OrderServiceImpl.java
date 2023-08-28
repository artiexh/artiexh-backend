package com.artiexh.api.service.impl;

import com.artiexh.api.service.CartService;
import com.artiexh.api.service.OrderService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.CartItemRepository;
import com.artiexh.data.jpa.repository.OrderRepository;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.data.jpa.repository.UserAddressRepository;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.domain.PaymentMethod;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.mapper.OrderMapper;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final CartItemRepository cartItemRepository;
	private final OrderMapper orderMapper;
	private final CartService cartService;

	@Transactional(isolation = Isolation.SERIALIZABLE)
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
		List<CartItemEntity> cartItemEntities = validateShopProductAndReduceQuantity(userId, checkoutRequest.getShops(), checkoutRequest.getPaymentMethod());

		// create order and order details
		try {
			List<OrderEntity> createdOrder = createOrder(userId, address, status, checkoutRequest.getShops(), cartItemEntities, checkoutRequest.getPaymentMethod());
			cartService.deleteItemToCart(
				userId,
				checkoutRequest.getShops().stream()
					.flatMap(checkoutShop -> checkoutShop.getItemIds().stream())
					.collect(Collectors.toSet())
			);
			return createdOrder;
		} catch (Exception ex) {
			rollbackShopProductQuantity(cartItemEntities);
			throw ex;
		}
	}

	private List<OrderEntity> onlinePaymentOrder(long userId, CheckoutRequest checkoutRequest) {
		// check common data
		UserAddressEntity address = getUserAddressEntity(userId, checkoutRequest.getAddressId());
		OrderStatus status = OrderStatus.PAYING;

		List<CartItemEntity> cartItemEntities = validateShopProductAndReduceQuantity(userId, checkoutRequest.getShops(), checkoutRequest.getPaymentMethod());

		// create order and order details
		try {
			return createOrder(userId, address, status, checkoutRequest.getShops(), cartItemEntities, checkoutRequest.getPaymentMethod());
		} catch (Exception ex) {
			rollbackShopProductQuantity(cartItemEntities);
			throw ex;
		}
	}

	//@Transactional
	public UserAddressEntity getUserAddressEntity(long userId, long addressId) {
		return userAddressRepository.findByIdAndUserId(addressId, userId)
			.orElseThrow(() -> new IllegalArgumentException("Address not existed"));
	}

	//@Transactional(isolation = Isolation.SERIALIZABLE)
	public List<CartItemEntity> validateShopProductAndReduceQuantity(long userId, Set<CheckoutShop> shops, PaymentMethod paymentMethod) {
		Set<CartItemId> itemIds = shops.stream()
			.flatMap(checkoutShop -> checkoutShop.getItemIds().stream())
			.map(itemId -> new CartItemId(userId, itemId))
			.collect(Collectors.toSet());

		List<CartItemEntity> cartItemEntities = cartItemRepository.findAllById(itemIds);
		if (itemIds.size() != cartItemEntities.size()) {
			throw new IllegalArgumentException("itemIds not valid");
		}

		for (var checkoutShop : shops) {
			for (var checkoutItem : checkoutShop.getItemIds()) {
				for (var cartItemEntity : cartItemEntities) {
					ProductEntity productEntity = cartItemEntity.getProduct();
					if (checkoutItem.equals(productEntity.getId()) && !checkoutShop.getShopId().equals(productEntity.getShop().getId())) {
						throw new IllegalArgumentException("shopId " + checkoutShop.getShopId() + " not contain itemId " + checkoutItem);
					}
				}
			}
		}

		for (var cartItemEntity : cartItemEntities) {
			if (ProductStatus.AVAILABLE.getByteValue() != cartItemEntity.getProduct().getStatus()) {
				throw new IllegalArgumentException("itemId " + cartItemEntity.getProduct().getId() + " not available");
			}

			Set<Byte> acceptedPaymentMethods = Set.of(cartItemEntity.getProduct().getPaymentMethods());
			if (!acceptedPaymentMethods.contains(paymentMethod.getByteValue())) {
				throw new IllegalArgumentException("Not accepted payment method for product id: " + cartItemEntity.getProduct().getId());
			}

			if (cartItemEntity.getQuantity() > cartItemEntity.getProduct().getRemainingQuantity()) {
				throw new IllegalArgumentException("Not enough quantity for product id: " + cartItemEntity.getProduct().getId());
			} else {
				cartItemEntity.getProduct().setRemainingQuantity(cartItemEntity.getProduct().getRemainingQuantity() - cartItemEntity.getQuantity());
			}
		}

		productRepository.saveAll(cartItemEntities.stream().map(CartItemEntity::getProduct).collect(Collectors.toSet()));
		return cartItemEntities;
	}

	//@Transactional
	public List<OrderEntity> createOrder(long userId, UserAddressEntity address, OrderStatus status,
										 Set<CheckoutShop> shops, List<CartItemEntity> cartItemEntities,
										 PaymentMethod paymentMethod) {
		Set<OrderEntity> orderEntities = shops.stream().map(checkoutShop ->
				OrderEntity.builder()
					.user(UserEntity.builder().id(userId).build())
					.shop(ArtistEntity.builder().id(checkoutShop.getShopId()).build())
					.shippingAddress(address)
					.note(checkoutShop.getNote())
					.paymentMethod(paymentMethod.getByteValue())
					.status(status.getByteValue())
					.orderDetails(cartItemEntities.stream()
						.filter(cartItemEntity -> cartItemEntity.getProduct().getShop().getId().equals(checkoutShop.getShopId()))
						.map(cartItemEntity -> OrderDetailEntity.builder()
							.product(cartItemEntity.getProduct())
							.quantity(cartItemEntity.getQuantity())
							.build()
						)
						.collect(Collectors.toSet())
					)
					.build()
			)
			.collect(Collectors.toSet());
		return orderRepository.saveAll(orderEntities);
	}

	//@Transactional(isolation = Isolation.SERIALIZABLE)
	public void rollbackShopProductQuantity(List<CartItemEntity> cartItemEntities) {
		Set<ProductEntity> productEntities = cartItemEntities.stream()
			.map(cartItemEntity -> {
				ProductEntity productEntity = cartItemEntity.getProduct();
				productEntity.setRemainingQuantity(productEntity.getRemainingQuantity() + cartItemEntity.getQuantity());
				return productEntity;
			})
			.collect(Collectors.toSet());
		productRepository.saveAll(productEntities);

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
}
