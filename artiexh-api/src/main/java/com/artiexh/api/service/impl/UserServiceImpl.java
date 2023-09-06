package com.artiexh.api.service.impl;

import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.OrderGroupService;
import com.artiexh.api.service.OrderService;
import com.artiexh.api.service.UserService;
import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.data.jpa.entity.OrderGroupEntity;
import com.artiexh.model.domain.OrderGroup;
import com.artiexh.model.mapper.OrderGroupMapper;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.user.UserOrderGroupResponse;
import com.artiexh.model.rest.user.UserOrderGroupResponsePage;
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
	private final OrderGroupService orderGroupService;
	private final OrderMapper orderMapper;
	private final OrderGroupMapper orderGroupMapper;

	@Override
	public UserOrderGroupResponse getOrderGroupById(Long id, Long userId) {
		OrderGroup order = orderGroupService.getById(id);
		if (!order.getUser().getId().equals(userId)) {
			throw new IllegalArgumentException(ErrorCode.ORDER_IS_INVALID.getMessage());
		}
		return orderGroupMapper.domainToUserResponse(order);
	}

	@Override
	public PageResponse<UserOrderGroupResponsePage> getOrderGroupInPage(Specification<OrderGroupEntity> specification, Pageable pageable) {
		Page<OrderGroup> orderPage = orderGroupService.getInPage(specification, pageable);
		return new PageResponse<>(orderPage.map(orderGroupMapper::domainToUserResponsePage));
	}

	@Override
	public Page<UserOrderResponsePage> getOrderInPage(Specification<OrderEntity> specification, Pageable pageable) {
		return null;
	}

	@Override
	public UserOrderResponse getOrderById(Long id, Long userId) {
		return null;
	}
}
