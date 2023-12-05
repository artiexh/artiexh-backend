package com.artiexh.api.service.impl;

import com.artiexh.api.base.common.Const;
import com.artiexh.api.base.exception.ArtiexhConfigException;
import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.base.exception.InvalidException;
import com.artiexh.api.base.service.SystemConfigService;
import com.artiexh.api.base.utils.SystemConfigHelper;
import com.artiexh.api.service.CampaignOrderService;
import com.artiexh.api.service.marketplace.ProductService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
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
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import com.artiexh.model.rest.order.request.UpdateShippingOrderRequest;
import com.artiexh.model.rest.order.user.response.AdminCampaignOrderResponsePage;
import com.artiexh.model.rest.order.user.response.CampaignOrderResponsePage;
import com.artiexh.model.rest.order.user.response.UserCampaignOrderDetailResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CampaignOrderServiceImpl implements CampaignOrderService {
	private final UserAddressRepository userAddressRepository;
	private final AccountRepository accountRepository;
	private final ProductService productService;
	private final CampaignOrderRepository campaignOrderRepository;
	private final OrderHistoryRepository orderHistoryRepository;
	private final GhtkOrderService ghtkOrderService;
	private final CampaignOrderMapper campaignOrderMapper;
	private final SystemConfigService systemConfigService;
	private final SystemConfigHelper systemConfigHelper;

	@Override
	public Page<CampaignOrderResponsePage> getCampaignOrderInPage(Specification<CampaignOrderEntity> specification, Pageable pageable) {
		return campaignOrderRepository.findAll(specification, pageable)
			.map(campaignOrderMapper::entityToUserResponsePage);
	}

	@Override
	public CampaignOrder getCampaignOrderById(Long orderId) {
		CampaignOrderEntity order = campaignOrderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
		CampaignOrder domain = campaignOrderMapper.entityToDomain(order);
		return domain;
	}

	@Override
	public Page<AdminCampaignOrderResponsePage> getAdminCampaignOrderInPage(Specification<CampaignOrderEntity> specification,
																			Pageable pageable) {
		return campaignOrderRepository.findAll(specification, pageable)
			.map(campaignOrderMapper::entityToAdminResponsePage);
	}

	@Override
	public AdminCampaignOrderResponse getAdminCampaignOrderById(Long orderId) {
		return campaignOrderRepository.findById(orderId)
			.map(campaignOrderMapper::entityToAdminResponse)
			.orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public UserCampaignOrderDetailResponse getOrderByIdAndOwner(Long orderId, Long artistId) {
		CampaignOrderEntity order = campaignOrderRepository.findByIdAndCampaignSaleOwnerId(orderId, artistId)
			.orElseThrow(EntityNotFoundException::new);
		return campaignOrderMapper.entityToUserDetailResponse(order);
	}

	@Override
	public UserCampaignOrderDetailResponse getOrderByIdAndUserId(Long orderId, Long userId) {
		CampaignOrderEntity order = campaignOrderRepository.findByIdAndOrderUserId(orderId, userId)
			.orElseThrow(EntityNotFoundException::new);
		return campaignOrderMapper.entityToUserDetailResponse(order);
	}

	@Override
	public Mono<ShipFeeResponse.ShipFee> getShippingFee(Long userId, GetShippingFeeRequest getShippingFeeRequest) {
		var addressEntity = userAddressRepository.findByIdAndUserId(getShippingFeeRequest.getAddressId(), userId)
			.orElseThrow(() -> new InvalidException(ErrorCode.USER_ADDRESS_NOT_FOUND));

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
				throw new InvalidException(
					ErrorCode.GET_GHTK_SHIPPING_FEE_FAILED, throwable.getResponseBodyAs(GhtkResponse.class).getMessage());
			})
			.map(ShipFeeResponse::getFee);
	}

	@Transactional
	@Override
	public AdminCampaignOrderResponse updateShippingOrderStatus(Long orderId,
																UpdateShippingOrderRequest updateShippingOrderRequest) {
		CampaignOrderEntity campaignOrderEntity = campaignOrderRepository.findById(orderId).orElseThrow(
			EntityNotFoundException::new);

		if (campaignOrderEntity.getStatus() != CampaignOrderStatus.PREPARING.getByteValue()) {
			throw new InvalidException(
				ErrorCode.UPDATE_CAMPAIGN_ORDER_STATUS_FAILED, "Không thể cập nhật trạng thái đơn hàng từ" + CampaignOrderStatus.fromValue(campaignOrderEntity.getStatus()) + " sang SHIPPING");
		}

		OrderEntity orderEntity = campaignOrderEntity.getOrder();

		var products = campaignOrderEntity.getOrderDetails().stream().map(orderDetailEntity -> {
			var productEntity = orderDetailEntity.getProduct();
			return com.artiexh.ghtk.client.model.order.Product.builder()
				.name(productEntity.getProductInventory().getName())
				.weight(Double.valueOf(productEntity.getProductInventory().getWeight()))
				.productCode(productEntity.getProductInventory().getProductCode()).build();
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
			.tags(updateShippingOrderRequest.getTags())
			.weightOption("gram");

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
				throw new InvalidException(
					ErrorCode.CREATE_GHTK_ORDER_FAILED, ((response != null && response.getMessage() != null) ? response.getMessage() : "Unknown response"));
			})
			.block();

		if (ghtkCreateOrderResponse == null) {
			throw new InvalidException(ErrorCode.CREATE_GHTK_ORDER_FAILED,"Không nhận được phản hồi từ Giao Hàng Tiết Kiệm");
		} else if (ghtkCreateOrderResponse.getOrder() == null) {
			throw new InvalidException(ErrorCode.CREATE_GHTK_ORDER_FAILED, ghtkCreateOrderResponse.getMessage());
		}

		campaignOrderEntity.setStatus(CampaignOrderStatus.SHIPPING.getByteValue());
		campaignOrderEntity.setShippingLabel(ghtkCreateOrderResponse.getOrder().getLabel());
		campaignOrderRepository.save(campaignOrderEntity);

		var orderHistoryEntity = OrderHistoryEntity.builder()
			.id(new OrderHistoryId(orderId, OrderHistoryStatus.SHIPPED.getByteValue()))
			.build();
		var savedOrderHistoryEntity = orderHistoryRepository.save(orderHistoryEntity);

		campaignOrderEntity.getOrderHistories().add(savedOrderHistoryEntity);
		return campaignOrderMapper.entityToAdminResponse(campaignOrderEntity);
	}

	@Transactional
	@Override
	public void cancelOrder(Long orderId, String message, Long updatedBy) {
		CampaignOrderEntity order = campaignOrderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

		if (!CampaignOrderStatus.ALLOWED_CANCEL_STATUS.contains(CampaignOrderStatus.fromValue(order.getStatus()))) {
			throw new InvalidException(ErrorCode.UPDATE_CAMPAIGN_ORDER_STATUS_FAILED, "Đơn hàng không thể bị hủy nếu như trạng thái không phải là PAYING hoặc REFUNDING");
		}

		AccountEntity account = accountRepository.findById(updatedBy).orElseThrow(EntityNotFoundException::new);
		if (!account.getId().equals(order.getOrder().getUser().getId())
			&& (account.getRole() != Role.ADMIN.getByteValue() &&
			account.getRole() != Role.STAFF.getByteValue())) {
			throw new InvalidException(ErrorCode.ORDER_STATUS_NOT_ALLOWED);
		}

		order.setStatus(CampaignOrderStatus.CANCELED.getByteValue());
		campaignOrderRepository.save(order);

		OrderHistoryEntity orderHistory = OrderHistoryEntity.builder()
			.id(OrderHistoryId.builder()
				.campaignOrderId(order.getId())
				.status(OrderHistoryStatus.CANCELED.getByteValue())
				.build())
			.datetime(Instant.now())
			.message(message)
			.updatedBy(account.getUsername())
			.build();
		orderHistoryRepository.save(orderHistory);
	}

	private void revertProductQuantity(Set<OrderDetailEntity> orderDetailEntities) {
		for (OrderDetailEntity orderDetail : orderDetailEntities) {
			ProductEntity productInSale = orderDetail.getProduct();
			productInSale.setSoldQuantity(productInSale.getSoldQuantity() - orderDetail.getQuantity());
			productService.update(productInSale);
		}
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	@Override
	public void refundOrder(Long orderId, Long updatedBy) {
		CampaignOrderEntity order = campaignOrderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

		if (!CampaignOrderStatus.ALLOWED_REFUNDING_STATUS.contains(CampaignOrderStatus.fromValue(order.getStatus()))) {
			throw new InvalidException(ErrorCode.UPDATE_CAMPAIGN_ORDER_STATUS_FAILED, "Đơn hàng không thể hoàn lại nếu như trạng thái không phải là PREPARING hoặc SHIPPING");
		}

		AccountEntity account = accountRepository.findById(updatedBy).orElseThrow(() -> new InvalidException(ErrorCode.ORDER_STATUS_NOT_ALLOWED));
		if (!account.getId().equals(order.getOrder().getUser().getId())
			&& (account.getRole() != Role.ADMIN.getByteValue() &&
			account.getRole() != Role.STAFF.getByteValue())) {
			throw new InvalidException(ErrorCode.ORDER_STATUS_NOT_ALLOWED);
		}

		order.setStatus(CampaignOrderStatus.REFUNDING.getByteValue());

		//Cancel ghtk order
		if (order.getStatus() == CampaignOrderStatus.SHIPPING.getByteValue()) {
			var cancelOrderGhtkResponse = ghtkOrderService.cancelOrder(order.getShippingLabel())
				.doOnError(WebClientResponseException.class, throwable -> {
					var response = throwable.getResponseBodyAs(GhtkResponse.class);
					throw new InvalidException(
						ErrorCode.CANCEL_GHTK_ORDER_FAILED, ((response != null && response.getMessage() != null) ? response.getMessage() : "Không nhận được phản hồi từ Giao Hàng Tiết Kiệm"));
				})
				.block();
			if (cancelOrderGhtkResponse == null || !cancelOrderGhtkResponse.isSuccess()) {
				throw new InvalidException(ErrorCode.CANCEL_GHTK_ORDER_FAILED, (cancelOrderGhtkResponse != null ? cancelOrderGhtkResponse.getMessage() : ""));
			}
		}

		order.setStatus(CampaignOrderStatus.REFUNDING.getByteValue());
		campaignOrderRepository.save(order);

		//Revert campaign product quantity
		revertProductQuantity(order.getOrderDetails());

		OrderHistoryEntity orderHistory = OrderHistoryEntity.builder()
			.id(OrderHistoryId.builder()
				.campaignOrderId(order.getId())
				.status(OrderHistoryStatus.REFUNDED.getByteValue())
				.build())
			.datetime(Instant.now())
			.updatedBy(account.getUsername())
			.build();
		orderHistoryRepository.save(orderHistory);
	}

}
