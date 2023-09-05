package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderGroup;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import com.artiexh.model.rest.order.request.PaymentQueryProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrderService {

	OrderGroup checkout(long userId, CheckoutRequest request);

	Page<Order> getInPage(Specification<OrderEntity> query, Pageable pageable);

	Order getById(Long orderId);

	Order updateOrderStatus(Long orderId, OrderStatus newStatus);

	ShipFeeResponse.ShipFee getShippingFee(Long userId, Long addressId, GetShippingFeeRequest request);

	String payment(Long id, PaymentQueryProperties paymentQueryProperties, Long userId);

	String confirmPayment(PaymentQueryProperties paymentQueryProperties);
}
