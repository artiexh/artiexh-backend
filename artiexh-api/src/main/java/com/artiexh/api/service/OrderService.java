package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.model.domain.Order;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrderService {

	Page<Order> getOrderInPage(Specification<OrderEntity> query, Pageable pageable);

	Order getOrderById(Long orderId);

	Order getOrderByIdAndShopId(Long orderId, Long shopId);

	Order getOrderByIdAndUserId(Long orderId, Long userId);

	ShipFeeResponse.ShipFee getShippingFee(Long userId, Long addressId, GetShippingFeeRequest request);

	void cancelOrder(OrderEntity order, String message, Long updatedBy);

	void refundOrder(OrderEntity order, Long createdBy);

}
