package com.artiexh.api.service.impl;

import com.artiexh.api.config.VnpConfigurationProperties;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.CartService;
import com.artiexh.api.service.OrderService;
import com.artiexh.api.utils.DateTimeUtils;
import com.artiexh.api.utils.PaymentUtils;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.data.jpa.projection.Bill;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.domain.PaymentMethod;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.mapper.OrderTransactionMapper;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import com.artiexh.model.rest.order.request.CheckoutShop;
import com.artiexh.model.rest.order.request.PaymentQueryProperties;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService {
	private static final Map<OrderStatus, Set<OrderStatus>> orderStatusChangeMap = Map.of(
		OrderStatus.PAYING, Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
		OrderStatus.PREPARING, Set.of(OrderStatus.SHIPPING, OrderStatus.CANCELLED),
		OrderStatus.SHIPPING, Set.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED)
	);

	private final UserAddressRepository userAddressRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final CartItemRepository cartItemRepository;
	private final OrderTransactionRepository orderTransactionRepository;
	private final OrderMapper orderMapper;
	private final CartService cartService;
	private final VnpConfigurationProperties vnpProperties;
	private final OrderTransactionMapper orderTransactionMapper;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	@Override
	public List<Order> checkout(long userId, CheckoutRequest checkoutRequest) {
		List<OrderEntity> orderEntities = switch (checkoutRequest.getPaymentMethod()) {
			case CASH -> cashPaymentOrder(userId, checkoutRequest);
			default -> onlinePaymentOrder(userId, checkoutRequest);
		};

		return orderEntities.stream().map(orderMapper::entityToResponseDomain).toList();
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

	private UserAddressEntity getUserAddressEntity(long userId, long addressId) {
		return userAddressRepository.findByIdAndUserId(addressId, userId)
			.orElseThrow(() -> new IllegalArgumentException("Address not existed"));
	}

	private List<CartItemEntity> validateShopProductAndReduceQuantity(long userId, Set<CheckoutShop> shops, PaymentMethod paymentMethod) {
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

	private List<OrderEntity> createOrder(long userId, UserAddressEntity address, OrderStatus status,
										  Set<CheckoutShop> shops, List<CartItemEntity> cartItemEntities,
										  PaymentMethod paymentMethod) {
		Set<OrderEntity> orderEntities = shops.stream().map(checkoutShop -> {
				var orderEntity = OrderEntity.builder()
					.user(UserEntity.builder().id(userId).build())
					.shop(ArtistEntity.builder().id(checkoutShop.getShopId()).build())
					.shippingAddress(address)
					.note(checkoutShop.getNote())
					.paymentMethod(paymentMethod.getByteValue())
					.status(status.getByteValue())
					.build();

				var savedOrderEntity = orderRepository.save(orderEntity);
				var orderDetailEntities = cartItemEntities.stream()
					.filter(cartItemEntity -> cartItemEntity.getProduct().getShop().getId().equals(checkoutShop.getShopId()))
					.map(cartItemEntity -> OrderDetailEntity.builder()
						.id(new OrderDetailId(savedOrderEntity.getId(), null))
						.product(cartItemEntity.getProduct())
						.quantity(cartItemEntity.getQuantity())
						.build()
					)
					.collect(Collectors.toSet());

				orderDetailRepository.saveAll(orderDetailEntities);

				return savedOrderEntity;
			})
			.collect(Collectors.toSet());
		return orderEntities.stream().toList();
	}

	private void rollbackShopProductQuantity(List<CartItemEntity> cartItemEntities) {
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
		OrderEntity order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
		OrderTransactionEntity orderTransaction = order.getOrderTransaction().stream().max(Comparator.comparing(OrderTransactionEntity::getPayDate)).orElse(null);
		Order domain = orderMapper.entityToResponseDomain(order);
		domain.setCurrentTransaction(orderTransactionMapper.entityToDomain(orderTransaction));
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

	@Override
	public String payment(Long id, PaymentQueryProperties paymentQueryProperties, Long userId) {
		Bill bill = orderRepository.getBillInfo(id);
		if (bill == null || bill.getPriceAmount() == null || bill.getPriceUnit() == null) {
			throw new EntityNotFoundException();
		}

		if (!userId.equals(bill.getOwnerId()) || bill.getStatus() != OrderStatus.PAYING.getByteValue()) {
			throw new IllegalArgumentException(ErrorCode.ORDER_IS_INVALID.getMessage());
		}

		String vnp_OrderInfo = "Thanh toan don hang " + id;

		return PaymentUtils.generatePaymentUrl(
			vnpProperties.getVersion(),
			vnpProperties.getCommand(),
			vnp_OrderInfo,
			id.toString(),
			paymentQueryProperties.getVnp_IpAddr(),
			vnpProperties.getTmnCode(),
			vnpProperties.getReturnUrl(),
			bill.getPriceAmount().multiply(new BigDecimal(100)).toBigInteger().toString(),
			bill.getPriceUnit(),
			"vn",
			vnpProperties.getUrl(),
			vnpProperties.getSecretKey()
		);
	}

	@Override
	public String confirmPayment(PaymentQueryProperties paymentQueryProperties) {
		String confirmUrl = vnpProperties.getFeConfirmUrl();
		OrderTransactionEntity orderTransaction = OrderTransactionEntity.builder()
			.transactionNo(paymentQueryProperties.getVnp_TransactionNo())
			.orderInfo(paymentQueryProperties.getVnp_OrderInfo())
			.bankCode(paymentQueryProperties.getVnp_BankCode())
			.cardType(paymentQueryProperties.getVnp_CardType())
			.payDate(DateTimeUtils.stringToInstant(paymentQueryProperties.getVnp_PayDate(), "yyyyMMddHHmmss"))
			.priceAmount(new BigDecimal(paymentQueryProperties.getVnp_Amount()).divide(new BigDecimal(100)))
			.responseCode(paymentQueryProperties.getVnp_ResponseCode())
			.transactionStatus(paymentQueryProperties.getVnp_TransactionStatus())
			.orderId(Long.parseLong(paymentQueryProperties.getVnp_TxnRef()))
			.build();
		orderTransactionRepository.save(orderTransaction);
		if (paymentQueryProperties.getVnp_ResponseCode().equals("00")) {
			log.info("Payment is done successfully. Transaction No" + paymentQueryProperties.getVnp_TransactionNo());
			updateOrderStatus(Long.parseLong(paymentQueryProperties.getVnp_TxnRef()), OrderStatus.PREPARING);
		}
		log.warn("Payment Transaction" + paymentQueryProperties.getVnp_TransactionNo() + " Status " + paymentQueryProperties.getVnp_TransactionStatus());
		log.warn("Payment Transaction" + paymentQueryProperties.getVnp_TransactionNo() + " Response Code " + paymentQueryProperties.getVnp_ResponseCode());
		return confirmUrl;
	}
}
