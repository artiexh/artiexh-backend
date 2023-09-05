package com.artiexh.api.service.impl;

import com.artiexh.api.service.OrderService;
import com.artiexh.api.service.UserService;
import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.domain.Order;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.user.UserOrderResponse;
import com.artiexh.model.rest.user.UserOrderResponsePage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final OrderService orderService;
	private final OrderMapper orderMapper;

	@Override
	public UserOrderResponse getOrderById(Long id, Long userId) {
		Order order = orderService.getById(id);
//		if (!order.getUser().getId().equals(userId)) {
//			throw new IllegalArgumentException(ErrorCode.ORDER_IS_INVALID.getMessage());
//		}
		return orderMapper.orderToUserResponse(order);
	}

	@Override
	public PageResponse<UserOrderResponsePage> getOrderInPage(Specification<OrderEntity> specification, Pageable pageable) {
		Page<Order> orderPage = orderService.getInPage(specification, pageable);
		return new PageResponse<>(orderPage.map(orderMapper::domainToUserResponsePage));
	}
}
