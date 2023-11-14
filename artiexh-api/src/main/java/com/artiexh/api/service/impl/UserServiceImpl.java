package com.artiexh.api.service.impl;

import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.service.CampaignOrderService;
import com.artiexh.api.service.OrderService;
import com.artiexh.api.service.UserService;
import com.artiexh.data.jpa.entity.CampaignOrderEntity;
import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.mapper.CampaignOrderMapper;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.rest.order.user.response.CampaignOrderResponsePage;
import com.artiexh.model.rest.order.user.response.DetailUserOrderResponse;
import com.artiexh.model.rest.order.user.response.UserCampaignOrderDetailResponse;
import com.artiexh.model.rest.order.user.response.UserOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final CampaignOrderService campaignOrderService;
	private final OrderService orderService;
	private final CampaignOrderMapper campaignOrderMapper;
	private final OrderMapper orderMapper;

	@Override
	public DetailUserOrderResponse getOrderById(Long id, Long userId) {
		try {
			return orderService.getUserDetailByIdAndUserId(id, userId);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(ErrorCode.ORDER_IS_INVALID.getMessage());
		}
	}

	@Override
	public Page<UserOrderResponse> getOrderInPage(Specification<OrderEntity> specification, Pageable pageable) {
		return orderService.getUserOrderInPage(specification, pageable);
	}

	@Override
	public Page<CampaignOrderResponsePage> getCampaignOrderInPage(Specification<CampaignOrderEntity> specification,
																  Pageable pageable) {
		return campaignOrderService.getCampaignOrderInPage(specification, pageable);
	}

	@Override
	public UserCampaignOrderDetailResponse getCampaignOrderById(Long id, Long userId) {
		return campaignOrderService.getOrderByIdAndUserId(id, userId);
	}
}
