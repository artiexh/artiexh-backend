package com.artiexh.api.service;

import com.artiexh.model.rest.order.request.CheckoutRequest;

public interface OrderService {

	void checkout(long userId, CheckoutRequest request);

}
