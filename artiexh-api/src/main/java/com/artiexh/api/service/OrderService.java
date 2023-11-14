package com.artiexh.api.service;

import com.artiexh.model.rest.order.request.CheckoutRequest;
import com.artiexh.model.rest.order.request.PaymentQueryProperties;
import com.artiexh.model.rest.order.user.response.DetailUserOrderResponse;

public interface OrderService {

	DetailUserOrderResponse checkout(long userId, CheckoutRequest request);

//	Page<Order> getInPage(Specification<OrderEntity> query, Pageable pageable);
//
//	Order getById(Long orderGroupId);

	String payment(Long id, PaymentQueryProperties paymentQueryProperties, Long userId, String confirmUrl);

	String confirmPayment(PaymentQueryProperties paymentQueryProperties);

}
