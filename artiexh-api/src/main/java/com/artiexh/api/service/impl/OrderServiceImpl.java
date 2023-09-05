package com.artiexh.api.service.impl;

import com.artiexh.api.config.VnpConfigurationProperties;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.CartService;
import com.artiexh.api.service.OrderService;
import com.artiexh.api.utils.DateTimeUtils;
import com.artiexh.api.utils.PaymentUtils;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.projection.Bill;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.ghtk.client.model.GhtkResponse;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeRequest;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.ghtk.client.service.GhtkOrderService;
import com.artiexh.model.domain.*;
import com.artiexh.model.mapper.OrderGroupMapper;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.mapper.OrderTransactionMapper;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import com.artiexh.model.rest.order.request.CheckoutShop;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
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
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private final OrderGroupRepository orderGroupRepository;
	private final OrderGroupMapper orderGroupMapper;
	private final OrderMapper orderMapper;
	private final CartService cartService;
	private final GhtkOrderService ghtkOrderService;
	private final ArtistRepository artistRepository;
	private final VnpConfigurationProperties vnpProperties;
	private final OrderTransactionMapper orderTransactionMapper;
	private final OrderHistoryRepository orderHistoryRepository;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	@Override
	public OrderGroup checkout(long userId, CheckoutRequest checkoutRequest) {
		OrderGroupEntity orderGroup = switch (checkoutRequest.getPaymentMethod()) {
			case CASH -> cashPaymentOrder(userId, checkoutRequest);
			default -> onlinePaymentOrder(userId, checkoutRequest);
		};

		return orderGroupMapper.entityToDomain(orderGroup);
	}

	private OrderGroupEntity cashPaymentOrder(long userId, CheckoutRequest checkoutRequest) {
		// check common data
		UserAddressEntity address = getUserAddressEntity(userId, checkoutRequest.getAddressId());
		OrderStatus status = OrderStatus.PREPARING;

		// check and reduce product quantity
		List<CartItemEntity> cartItemEntities = validateShopProductAndReduceQuantity(userId, checkoutRequest.getShops(), checkoutRequest.getPaymentMethod());

		// create order and order details
		try {
			OrderGroupEntity createdOrder = createOrder(userId, address, status, checkoutRequest.getShops(), cartItemEntities, checkoutRequest.getPaymentMethod());
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

	private OrderGroupEntity onlinePaymentOrder(long userId, CheckoutRequest checkoutRequest) {
		// check common data
		UserAddressEntity address = getUserAddressEntity(userId, checkoutRequest.getAddressId());
		OrderStatus status = OrderStatus.PAYING;

		List<CartItemEntity> cartItemEntities = validateShopProductAndReduceQuantity(userId, checkoutRequest.getShops(), checkoutRequest.getPaymentMethod());

		// create order and order details
		try {
			OrderGroupEntity createdOrder = createOrder(userId, address, status, checkoutRequest.getShops(), cartItemEntities, checkoutRequest.getPaymentMethod());
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

	private OrderGroupEntity createOrder(long userId, UserAddressEntity address, OrderStatus status,
										 Set<CheckoutShop> shops, List<CartItemEntity> cartItemEntities,
										 PaymentMethod paymentMethod) {
		OrderGroupEntity orderGroupEntity = new OrderGroupEntity();
		orderGroupEntity.setShippingAddress(address);
		orderGroupEntity.setUser(UserEntity.builder().id(userId).build());
		orderGroupRepository.save(orderGroupEntity);

		Set<OrderEntity> orderEntities = shops.stream().map(checkoutShop -> {
				var orderEntity = OrderEntity.builder()
					//.user(UserEntity.builder().id(userId).build())
					.shop(ArtistEntity.builder().id(checkoutShop.getShopId()).build())
					//.shippingAddress(address)
					.note(checkoutShop.getNote())
					.paymentMethod(paymentMethod.getByteValue())
					.status(status.getByteValue())
					.orderGroupId(orderGroupEntity.getId())
					.shippingFee(checkoutShop.getShippingFee())
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

				orderHistoryRepository.save(OrderHistoryEntity.builder()
					.id(new OrderHistoryEntityId(savedOrderEntity.getId(), OrderHistoryStatus.CREATED.getByteValue()))
					.build()
				);

				savedOrderEntity.setOrderDetails(orderDetailEntities);
				return savedOrderEntity;
			})
			.collect(Collectors.toSet());

		orderGroupEntity.setOrders(orderEntities);
		return orderGroupEntity;
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
		//OrderTransactionEntity orderTransaction = order.getOrderTransaction().stream().max(Comparator.comparing(OrderTransactionEntity::getPayDate)).orElse(null);
		Order domain = orderMapper.entityToResponseDomain(order);
		//domain.setCurrentTransaction(orderTransactionMapper.entityToDomain(orderTransaction));
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

	@Override
	public String payment(Long id, PaymentQueryProperties paymentQueryProperties, Long userId) {
		List<Bill> bills = orderGroupRepository.getBillInfo(id);

		if (bills == null || bills.isEmpty()) {
			throw new EntityNotFoundException();
		}

		if (!userId.equals(bills.get(0).getOwnerId()) || !bills.get(0).getStatus().equals(OrderStatus.PAYING.getByteValue())) {
			throw new IllegalArgumentException(ErrorCode.ORDER_IS_INVALID.getMessage());
		}

		BigDecimal totalPrice = BigDecimal.ZERO;
		for (Bill bill : bills) {
			totalPrice = totalPrice.add(bill.getOrderAmount());

			//TODO: Calculate with voucher (shop + system)
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
			totalPrice.multiply(new BigDecimal(100)).toBigInteger().toString(),
			bills.get(0).getPriceUnit(),
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
			.orderGroupId(Long.parseLong(paymentQueryProperties.getVnp_TxnRef()))
			.build();
		orderTransactionRepository.saveAndFlush(orderTransaction);
		if (paymentQueryProperties.getVnp_ResponseCode().equals("00")) {
			log.info("Payment is done successfully. Transaction No" + paymentQueryProperties.getVnp_TransactionNo());
			orderRepository.updatePayment(Long.parseLong(paymentQueryProperties.getVnp_TxnRef()));
		}
		log.warn("Payment Transaction" + paymentQueryProperties.getVnp_TransactionNo() + " Status " + paymentQueryProperties.getVnp_TransactionStatus());
		log.warn("Payment Transaction" + paymentQueryProperties.getVnp_TransactionNo() + " Response Code " + paymentQueryProperties.getVnp_ResponseCode());
		return confirmUrl;
	}
}
