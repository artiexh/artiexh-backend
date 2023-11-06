package com.artiexh.api.service.impl;

import com.artiexh.api.base.common.Const;
import com.artiexh.api.base.exception.ArtiexhConfigException;
import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.base.exception.IllegalAccessException;
import com.artiexh.api.base.service.SystemConfigService;
import com.artiexh.api.base.utils.SystemConfigHelper;
import com.artiexh.api.service.CampaignOrderService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.AccountRepository;
import com.artiexh.data.jpa.repository.CampaignOrderRepository;
import com.artiexh.data.jpa.repository.OrderHistoryRepository;
import com.artiexh.data.jpa.repository.UserAddressRepository;
import com.artiexh.ghtk.client.model.GhtkResponse;
import com.artiexh.ghtk.client.model.order.CreateOrderRequest;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeRequest;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.ghtk.client.service.GhtkOrderService;
import com.artiexh.model.domain.CampaignOrder;
import com.artiexh.model.domain.CampaignOrderStatus;
import com.artiexh.model.domain.OrderHistoryStatus;
import com.artiexh.model.domain.Role;
import com.artiexh.model.mapper.CampaignOrderMapper;
import com.artiexh.model.rest.order.admin.response.AdminCampaignOrderResponse;
import com.artiexh.model.rest.order.admin.response.AdminCampaignOrderResponsePage;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import com.artiexh.model.rest.order.request.UpdateShippingOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CampaignOrderServiceImpl implements CampaignOrderService {
	private final UserAddressRepository userAddressRepository;
	private final AccountRepository accountRepository;
	private final CampaignOrderRepository campaignOrderRepository;
	private final OrderHistoryRepository orderHistoryRepository;
	private final GhtkOrderService ghtkOrderService;
	private final CampaignOrderMapper campaignOrderMapper;
	private final SystemConfigService systemConfigService;
	private final SystemConfigHelper systemConfigHelper;

	@Override
	public Page<CampaignOrder> getCampaignOrderInPage(Specification<CampaignOrderEntity> specification, Pageable pageable) {
		Page<CampaignOrderEntity> entities = campaignOrderRepository.findAll(specification, pageable);
		return entities.map(order -> {
			CampaignOrder domain = campaignOrderMapper.entityToResponseDomain(order);
			return domain;
		});
	}

	@Override
	public CampaignOrder getCampaignOrderById(Long orderId) {
		CampaignOrderEntity order = campaignOrderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
		CampaignOrder domain = campaignOrderMapper.entityToResponseDomain(order);
		return domain;
	}

	@Override
	public Page<AdminCampaignOrderResponsePage> getAdminCampaignOrderInPage(Specification<CampaignOrderEntity> specification,
																			Pageable pageable) {
		return campaignOrderRepository.findAll(specification, pageable)
			.map(campaignOrderMapper::entityToUserResponse);
	}

	@Override
	public AdminCampaignOrderResponse getAdminCampaignOrderById(Long orderId) {
		return campaignOrderRepository.findById(orderId)
			.map(campaignOrderMapper::entityToUserResponse)
			.orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public CampaignOrder getOrderByIdAndOwner(Long orderId, Long artistId) {
		CampaignOrderEntity order = campaignOrderRepository.findByIdAndCampaignOwnerId(orderId, artistId)
			.orElseThrow(EntityNotFoundException::new);
		CampaignOrder domain = campaignOrderMapper.entityToResponseDomain(order);
		return domain;
	}

	@Override
	public CampaignOrder getOrderByIdAndUserId(Long orderId, Long userId) {
		CampaignOrderEntity order = campaignOrderRepository.findByIdAndOrderUserId(orderId, userId)
			.orElseThrow(EntityNotFoundException::new);
		CampaignOrder domain = campaignOrderMapper.entityToResponseDomain(order);
		return domain;
	}

	@Override
	public Mono<ShipFeeResponse.ShipFee> getShippingFee(Long userId, GetShippingFeeRequest getShippingFeeRequest) {
		var addressEntity = userAddressRepository.findByIdAndUserId(getShippingFeeRequest.getAddressId(), userId)
			.orElseThrow(() -> new IllegalArgumentException("AddressId not belong to user"));

		String artiexhPickAddress = systemConfigService.getOrThrow(
			Const.SystemConfigKey.ARTIEXH_PICK_ADDRESS,
			() -> new ArtiexhConfigException("Artiexh address is not configured")
		);
		WardEntity artiexhPickWard = systemConfigHelper.getArtiexhWardEntity();

		var request = ShipFeeRequest.builder()
			.pickAddress(artiexhPickAddress)
			.pickProvince(artiexhPickWard.getDistrict().getProvince().getFullName())
			.pickDistrict(artiexhPickWard.getDistrict().getFullName())
			.pickWard(artiexhPickWard.getFullName())
			.address(addressEntity.getAddress())
			.province(addressEntity.getWard().getDistrict().getProvince().getFullName())
			.district(addressEntity.getWard().getDistrict().getFullName())
			.ward(addressEntity.getWard().getFullName())
			.weight(getShippingFeeRequest.getTotalWeight())
			.deliverOption("none")
			.tags(getShippingFeeRequest.getTags())
			.build();

		return ghtkOrderService.getShipFee(request)
			.doOnError(WebClientResponseException.class, throwable -> {
				throw new IllegalArgumentException(
					"Create ghtk order failed: " + throwable.getResponseBodyAs(GhtkResponse.class).getMessage());
			})
			.map(ShipFeeResponse::getFee);
	}

	@Transactional
	@Override
	public AdminCampaignOrderResponse updateShippingOrderStatus(Long orderId,
																UpdateShippingOrderRequest updateShippingOrderRequest) {
		CampaignOrderEntity campaignOrderEntity = campaignOrderRepository.findById(orderId).orElseThrow(
			() -> new IllegalArgumentException("campaignOrderId " + orderId + " not found"));

		if (campaignOrderEntity.getStatus() != CampaignOrderStatus.PREPARING.getByteValue()) {
			throw new IllegalArgumentException(
				"Cannot update order status from " + CampaignOrderStatus.fromValue(campaignOrderEntity.getStatus()) + " to SHIPPING");
		}

		OrderEntity orderEntity = campaignOrderEntity.getOrder();

		var products = campaignOrderEntity.getOrderDetails().stream().map(orderDetailEntity -> {
			var productEntity = orderDetailEntity.getProduct();
			return com.artiexh.ghtk.client.model.order.Product.builder().name(productEntity.getName())
				.weight(Double.valueOf(productEntity.getWeight())).productCode("").build();
		}).collect(Collectors.toSet());

		var orderBuilder = CreateOrderRequest.Order.builder()
			.id(campaignOrderEntity.getId().toString())
			.pickMoney(0) // no cod
			.name(campaignOrderEntity.getOrder().getDeliveryName())
			.address(campaignOrderEntity.getOrder().getDeliveryAddress())
			.province(campaignOrderEntity.getOrder().getDeliveryProvince())
			.district(campaignOrderEntity.getOrder().getDeliveryDistrict())
			.ward(campaignOrderEntity.getOrder().getDeliveryWard())
			.pickName(campaignOrderEntity.getOrder().getPickName())
			.pickAddress(orderEntity.getPickAddress())
			.pickProvince(orderEntity.getPickProvince())
			.pickDistrict(orderEntity.getPickDistrict())
			.pickWard(orderEntity.getPickWard())
			.pickTel(orderEntity.getPickTel())
			.pickEmail(orderEntity.getPickEmail())
			.hamlet("Khác")
			.tel(campaignOrderEntity.getOrder().getDeliveryTel())
			.email(campaignOrderEntity.getOrder().getDeliveryEmail())
			.note(updateShippingOrderRequest.getNote())
			.tags(updateShippingOrderRequest.getTags());

		if (updateShippingOrderRequest.getValue() != null) {
			orderBuilder.value(updateShippingOrderRequest.getValue().intValue());
		} else {
			var value = campaignOrderEntity.getOrderDetails().stream()
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

		campaignOrderEntity.setStatus(CampaignOrderStatus.SHIPPING.getByteValue());
		campaignOrderEntity.setShippingLabel(ghtkCreateOrderResponse.getOrder().getLabel());
		campaignOrderRepository.save(campaignOrderEntity);

		var orderHistoryEntity = OrderHistoryEntity.builder()
			.id(new OrderHistoryEntityId(orderId, OrderHistoryStatus.SHIPPED.getByteValue()))
			.build();
		var savedOrderHistoryEntity = orderHistoryRepository.save(orderHistoryEntity);

		campaignOrderEntity.getOrderHistories().add(savedOrderHistoryEntity);
		return campaignOrderMapper.entityToUserResponse(campaignOrderEntity);
	}

	@Transactional
	@Override
	public void cancelOrder(Long orderId, String message, Long updatedBy) {
		CampaignOrderEntity order = campaignOrderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

		AccountEntity account = accountRepository.findById(updatedBy).orElseThrow(EntityNotFoundException::new);
		if (!account.getId().equals(order.getOrder().getUser().getId())
			&& (account.getRole() != Role.ADMIN.getByteValue() &&
			account.getRole() != Role.STAFF.getByteValue())) {
			throw new IllegalAccessException(ErrorCode.ORDER_STATUS_NOT_ALLOWED.getMessage());
		}
		if (order.getStatus() != CampaignOrderStatus.PAYING.getByteValue() &&
			order.getStatus() != CampaignOrderStatus.PREPARING.getByteValue()) {
			throw new IllegalArgumentException("Order can not be canceled if order's status are not PAYING or PREPARING");
		}
		order.setStatus(CampaignOrderStatus.CANCELED.getByteValue());
		//TODO: revert campaign product quantity
		campaignOrderRepository.save(order);

		//TODO: update field updatedBy for history
		OrderHistoryEntity orderHistory = OrderHistoryEntity.builder()
			.id(OrderHistoryEntityId.builder()
				.campaignOrderId(order.getId())
				.status(OrderHistoryStatus.CANCELED.getByteValue())
				.build())
			.datetime(Instant.now())
			.message(message)
			.build();
		orderHistoryRepository.save(orderHistory);
	}

	@Transactional
	@Override
	public void refundOrder(Long orderId, Long updatedBy) {
		CampaignOrderEntity order = campaignOrderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
		if (!order.getStatus().equals(CampaignOrderStatus.CANCELED.getByteValue())) {
			throw new IllegalArgumentException("Order can not be refunded before CANCELED");
		}
		order.setStatus(CampaignOrderStatus.REFUNDED.getByteValue());
		campaignOrderRepository.save(order);

		//TODO: update field updatedBy for history
		OrderHistoryEntity orderHistory = OrderHistoryEntity.builder()
			.id(OrderHistoryEntityId.builder()
				.campaignOrderId(order.getId())
				.status(OrderHistoryStatus.REFUNDED.getByteValue())
				.build())
			.datetime(Instant.now())
			.build();
		orderHistoryRepository.save(orderHistory);
	}

}
