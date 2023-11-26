package com.artiexh.api.service.impl;

import com.artiexh.api.base.common.Const;
import com.artiexh.api.base.exception.ArtiexhConfigException;
import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.base.service.SystemConfigService;
import com.artiexh.api.base.utils.DateTimeUtils;
import com.artiexh.api.base.utils.PaymentUtils;
import com.artiexh.api.base.utils.SystemConfigHelper;
import com.artiexh.api.config.VnpConfigurationProperties;
import com.artiexh.api.service.CartService;
import com.artiexh.api.service.OrderService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.projection.Bill;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.domain.CampaignOrderStatus;
import com.artiexh.model.domain.OrderHistoryStatus;
import com.artiexh.model.domain.PaymentMethod;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.rest.order.request.CheckoutCampaign;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import com.artiexh.model.rest.order.request.PaymentQueryProperties;
import com.artiexh.model.rest.order.user.response.DetailUserOrderResponse;
import com.artiexh.model.rest.order.user.response.UserOrderResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService {
	private final CartRepository cartRepository;
	private final CampaignSaleRepository campaignSaleRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final CartItemRepository cartItemRepository;
	private final OrderMapper orderMapper;
	private final CartService cartService;
	private final OrderTransactionRepository orderTransactionRepository;
	private final OrderRepository orderRepository;
	private final VnpConfigurationProperties vnpProperties;
	private final OrderHistoryRepository orderHistoryRepository;
	private final UserAddressRepository userAddressRepository;
	private final CampaignOrderRepository campaignOrderRepository;
	private final StringRedisTemplate redisTemplate;
	private final SystemConfigService systemConfigService;
	private final SystemConfigHelper systemConfigHelper;

	@Override
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public DetailUserOrderResponse checkout(long userId, CheckoutRequest request) {
		OrderEntity order = switch (request.getPaymentMethod()) {
			case CASH -> cashPaymentOrder(userId, request);
			default -> vnPaymentOrder(userId, request);
		};

		return orderMapper.entityToUserDetailResponse(order);
	}

	private OrderEntity cashPaymentOrder(long userId, CheckoutRequest checkoutRequest) {
		throw new UnsupportedOperationException("Cash payment is not supported yet");
	}

	private OrderEntity vnPaymentOrder(long userId, CheckoutRequest checkoutRequest) {
		// check common data
		var userEntity = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not existed"));

		var address = getUserAddressEntity(userId, checkoutRequest.getAddressId());
		CampaignOrderStatus status = CampaignOrderStatus.PAYING;

		List<CartItemEntity> cartItemEntities = validateCampaignProductAndReduceQuantity(
			userId,
			checkoutRequest.getCampaigns(),
			checkoutRequest.getPaymentMethod()
		);

		// create order and order details
		try {
			OrderEntity createdOrder = createOrder(
				userEntity,
				address,
				status,
				checkoutRequest.getCampaigns(),
				cartItemEntities,
				checkoutRequest.getPaymentMethod()
			);

			cartService.deleteItemToCart(
				checkoutRequest.getCampaigns().stream()
					.flatMap(checkoutCampaign -> checkoutCampaign.getItemIds().stream()
						.map(productCode -> new CartItemId(userId, checkoutCampaign.getCampaignId(), productCode))
					)
					.collect(Collectors.toSet())
			);
			return createdOrder;
		} catch (Exception ex) {
			rollbackProductQuantity(cartItemEntities);
			throw ex;
		}
	}

	private UserAddressEntity getUserAddressEntity(long userId, long addressId) {
		return userAddressRepository.findByIdAndUserId(addressId, userId)
			.orElseThrow(() -> new IllegalArgumentException("Address not existed"));
	}

	private List<CartItemEntity> validateCampaignProductAndReduceQuantity(long userId,
																		  Set<CheckoutCampaign> campaigns,
																		  PaymentMethod paymentMethod) {
		Set<CartItemId> itemIds = campaigns.stream()
			.flatMap(checkoutCampaign -> checkoutCampaign.getItemIds().stream()
				.map(itemId -> new CartItemId(userId, checkoutCampaign.getCampaignId(), itemId)))
			.collect(Collectors.toSet());

		List<CartItemEntity> cartItemEntities = cartItemRepository.findAllById(itemIds);
		if (itemIds.size() != cartItemEntities.size()) {
			throw new IllegalArgumentException("itemIds not valid");
		}

		Instant now = Instant.now();
		campaignSaleRepository.findAllById(campaigns.stream().map(CheckoutCampaign::getCampaignId).collect(Collectors.toSet()))
			.forEach(campaignSaleEntity -> {
				if (now.isBefore(campaignSaleEntity.getFrom()) || now.isAfter(campaignSaleEntity.getTo())) {
					throw new IllegalArgumentException("campaignSaleId " + campaignSaleEntity.getId() + " open from " + campaignSaleEntity.getFrom() + " to " + campaignSaleEntity.getTo());
				}
			});

		for (var cartItemEntity : cartItemEntities) {
			if (ProductStatus.AVAILABLE.getByteValue() != cartItemEntity.getProduct().getProductInventory().getStatus()) {
				throw new IllegalArgumentException("itemId " + cartItemEntity.getProduct().getId() + " not available");
			}

			Set<Byte> acceptedPaymentMethods = Set.of(cartItemEntity.getProduct().getProductInventory().getPaymentMethods());
			if (!acceptedPaymentMethods.contains(paymentMethod.getByteValue())) {
				throw new IllegalArgumentException("Not accepted payment method for product id: " + cartItemEntity.getProduct().getId());
			}

			if (cartItemEntity.getProduct().getProductInventory().getMaxItemsPerOrder() != null
				&& cartItemEntity.getQuantity() > cartItemEntity.getProduct().getProductInventory().getMaxItemsPerOrder()) {
				throw new IllegalArgumentException("Exceed max items per order for product id: " + cartItemEntity.getProduct().getId());
			}

			if (cartItemEntity.getQuantity() > (cartItemEntity.getProduct().getQuantity() - cartItemEntity.getProduct().getSoldQuantity())) {
				throw new IllegalArgumentException("Not enough quantity for product id: " + cartItemEntity.getProduct().getId());
			} else {
				cartItemEntity.getProduct().setSoldQuantity(cartItemEntity.getProduct().getSoldQuantity() + cartItemEntity.getQuantity());
			}
		}

		productRepository.saveAll(cartItemEntities.stream().map(CartItemEntity::getProduct).collect(Collectors.toSet()));
		return cartItemEntities;
	}

	private OrderEntity createOrder(UserEntity userEntity, UserAddressEntity address, CampaignOrderStatus status,
									Set<CheckoutCampaign> campaigns, List<CartItemEntity> cartItemEntities,
									PaymentMethod paymentMethod) {
		String artiexhPickAddress = systemConfigService.getOrThrow(
			Const.SystemConfigKey.ARTIEXH_PICK_ADDRESS,
			() -> new ArtiexhConfigException("Artiexh address is not configured")
		);
		String artiexhPickPhone = systemConfigService.getOrThrow(
			Const.SystemConfigKey.ARTIEXH_PICK_PHONE,
			() -> new ArtiexhConfigException("Artiexh phone is not configured")
		);
		String artiexhPickEmail = systemConfigService.getOrThrow(
			Const.SystemConfigKey.ARTIEXH_PICK_EMAIL,
			() -> new ArtiexhConfigException("Artiexh email is not configured")
		);
		String artiexhPickName = systemConfigService.getOrThrow(
			Const.SystemConfigKey.ARTIEXH_PICK_NAME,
			() -> new ArtiexhConfigException("Artiexh name is not configured")
		);
		WardEntity artiexhPickWard = systemConfigHelper.getArtiexhWardEntity();

		var orderEntity = OrderEntity.builder()
			.deliveryName(userEntity.getDisplayName())
			.deliveryAddress(address.getAddress())
			.deliveryWard(address.getWard().getFullName())
			.deliveryDistrict(address.getWard().getDistrict().getFullName())
			.deliveryProvince(address.getWard().getDistrict().getProvince().getFullName())
			.deliveryCountry(address.getWard().getDistrict().getProvince().getCountry().getName())
			.deliveryTel(address.getPhone())
			.deliveryEmail(userEntity.getEmail())
			.pickAddress(artiexhPickAddress)
			.pickWard(artiexhPickWard.getFullName())
			.pickDistrict(artiexhPickWard.getDistrict().getFullName())
			.pickProvince(artiexhPickWard.getDistrict().getProvince().getFullName())
			.pickTel(artiexhPickPhone)
			.pickName(artiexhPickName)
			.pickEmail(artiexhPickEmail)
			.user(userEntity)
			.paymentMethod(paymentMethod.getByteValue())
			.build();

		var savedOrderEntity = orderRepository.save(orderEntity);

		Set<CampaignOrderEntity> campaignOrderEntities = campaigns.stream().map(checkoutCampaign -> {
				var campaignOrderEntity = CampaignOrderEntity.builder()
					.campaignSale(campaignSaleRepository.getReferenceById(checkoutCampaign.getCampaignId()))
					.note(checkoutCampaign.getNote())
					.status(status.getByteValue())
					.order(savedOrderEntity)
					.shippingFee(checkoutCampaign.getShippingFee())
					.build();

				var savedCampaignOrderEntity = campaignOrderRepository.save(campaignOrderEntity);
				var orderDetailEntities = cartItemEntities.stream()
					.filter(cartItemEntity -> cartItemEntity.getProduct().getCampaignSale().getId().equals(checkoutCampaign.getCampaignId()))
					.map(cartItemEntity -> OrderDetailEntity.builder()
						.id(new OrderDetailId(
							savedCampaignOrderEntity.getId(),
							cartItemEntity.getProduct().getId().getCampaignSaleId(),
							cartItemEntity.getProduct().getId().getProductCode())
						)
						.campaignOrder(savedCampaignOrderEntity)
						.product(cartItemEntity.getProduct())
						.quantity(cartItemEntity.getQuantity())
						.build()
					)
					.collect(Collectors.toSet());

				orderDetailRepository.saveAll(orderDetailEntities);

				orderHistoryRepository.save(OrderHistoryEntity.builder()
					.id(new OrderHistoryId(savedCampaignOrderEntity.getId(), OrderHistoryStatus.CREATED.getByteValue()))
					.build()
				);

				savedCampaignOrderEntity.setOrderDetails(orderDetailEntities);
				return savedCampaignOrderEntity;
			})
			.collect(Collectors.toSet());

		savedOrderEntity.setCampaignOrders(campaignOrderEntities);
		return savedOrderEntity;
	}

	private void rollbackProductQuantity(List<CartItemEntity> cartItemEntities) {
		Set<ProductEntity> productEntities = cartItemEntities.stream()
			.map(cartItemEntity -> {
				ProductEntity productEntity = cartItemEntity.getProduct();
				productEntity.setSoldQuantity(productEntity.getSoldQuantity() + cartItemEntity.getQuantity());
				return productEntity;
			})
			.collect(Collectors.toSet());
		productRepository.saveAll(productEntities);

	}

	@Override
	public Page<UserOrderResponse> getUserOrderInPage(Specification<OrderEntity> specification, Pageable pageable) {
		return orderRepository.findAll(specification, pageable)
			.map(orderMapper::entityToUserResponse);
	}

	@Override
	public DetailUserOrderResponse getUserDetailById(Long orderId) {
		OrderEntity order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
		return orderMapper.entityToUserDetailResponse(order);
	}

	@Override
	public DetailUserOrderResponse getUserDetailByIdAndUserId(Long orderId, Long userId) {
		OrderEntity order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(IllegalArgumentException::new);
		return orderMapper.entityToUserDetailResponse(order);
	}

	@Override
	public String payment(Long id, PaymentQueryProperties paymentQueryProperties, Long userId, String confirmUrl) {
		List<Bill> bills = orderRepository.getBillInfo(id);

		if (bills == null || bills.isEmpty()) {
			throw new EntityNotFoundException();
		}

		if (!userId.equals(bills.get(0).getOwnerId()) || !bills.get(0).getStatus().equals(CampaignOrderStatus.PAYING.getByteValue())) {
			throw new IllegalArgumentException(ErrorCode.ORDER_IS_INVALID.getMessage());
		}

		BigDecimal totalPrice = BigDecimal.ZERO;
		for (Bill bill : bills) {
			totalPrice = totalPrice.add(bill.getOrderAmount());
		}

		redisTemplate.boundValueOps("payment_confirm_url_" + id).set(confirmUrl);

		String vnpOrderInfo = "Thanh toan don hang " + id;

		return PaymentUtils.generatePaymentUrl(
			vnpProperties.getVersion(),
			vnpProperties.getCommand(),
			vnpOrderInfo,
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
	@Transactional
	public String confirmPayment(PaymentQueryProperties paymentQueryProperties) {
		OrderTransactionEntity orderTransaction = OrderTransactionEntity.builder()
			.transactionNo(paymentQueryProperties.getVnp_TransactionNo())
			.orderInfo(paymentQueryProperties.getVnp_OrderInfo())
			.bankCode(paymentQueryProperties.getVnp_BankCode())
			.cardType(paymentQueryProperties.getVnp_CardType())
			.payDate(DateTimeUtils.stringToInstant(paymentQueryProperties.getVnp_PayDate(), "yyyyMMddHHmmss", ZoneId.of("Asia/Ho_Chi_Minh")))
			.priceAmount(new BigDecimal(paymentQueryProperties.getVnp_Amount()).divide(new BigDecimal(100)))
			.responseCode(paymentQueryProperties.getVnp_ResponseCode())
			.transactionStatus(paymentQueryProperties.getVnp_TransactionStatus())
			.orderId(Long.parseLong(paymentQueryProperties.getVnp_TxnRef()))
			.build();
		orderTransactionRepository.save(orderTransaction);
		if (paymentQueryProperties.getVnp_ResponseCode().equals("00")) {
			log.info("Payment is done successfully. Transaction No" + paymentQueryProperties.getVnp_TransactionNo());

			long orderId = Long.parseLong(paymentQueryProperties.getVnp_TxnRef());

			var orderHistoryEntities = campaignOrderRepository.getAllByOrderId(orderId).stream()
				.map(CampaignOrderEntity::getId)
				.map(campaignOrderId -> OrderHistoryEntity.builder()
					.id(new OrderHistoryId(campaignOrderId, OrderHistoryStatus.PAID.getByteValue()))
					.build()
				)
				.collect(Collectors.toSet());

			orderHistoryRepository.saveAll(orderHistoryEntities);

			campaignOrderRepository.updatePayment(orderId);
		}
		log.info("Payment Transaction " + paymentQueryProperties.getVnp_TransactionNo() + " Status " + paymentQueryProperties.getVnp_TransactionStatus());
		log.info("Payment Transaction " + paymentQueryProperties.getVnp_TransactionNo() + " Response Code " + paymentQueryProperties.getVnp_ResponseCode());

		return redisTemplate.boundValueOps("payment_confirm_url_" + paymentQueryProperties.getVnp_TxnRef())
			.getAndDelete();
	}
}
