package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrderService {

	Page<Order> getOrderInPage(Specification<OrderEntity> query, Pageable pageable);

	Order getOrderById(Long orderId);

	Order updateOrderStatus(Long orderId, OrderStatus newStatus);

	ShipFeeResponse.ShipFee getShippingFee(Long userId, Long addressId, GetShippingFeeRequest request);

}
