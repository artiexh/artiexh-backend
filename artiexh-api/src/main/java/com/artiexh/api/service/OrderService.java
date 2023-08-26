package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.domain.Order;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface OrderService {

	List<Order> checkout(long userId, CheckoutRequest request);

	Page<Order> getInPage(Specification<OrderEntity> query, Pageable pageable);

	Order getById(Long orderId);
}
