package com.artiexh.api.service.impl;

import com.artiexh.api.service.CampaignOrderService;
import com.artiexh.api.service.OrderService;
import com.artiexh.api.service.UserService;
import com.artiexh.model.mapper.CampaignOrderMapper;
import com.artiexh.model.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
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

//	@Override
//	public DetailUserOrderResponse getOrderById(Long id, Long userId) {
//		Order order = orderService.getById(id);
//		if (!order.getUser().getId().equals(userId)) {
//			throw new IllegalArgumentException(ErrorCode.ORDER_IS_INVALID.getMessage());
//		}
//		return orderMapper.domainToUserDetailResponse(order);
//	}
//
//	@Override
//	public Page<UserOrderResponse> getOrderInPage(Specification<OrderEntity> specification, Pageable pageable) {
//		return orderService.getInPage(specification, pageable)
//			.map(orderMapper::domainToUserResponse);
//	}

//	@Override
//	public Page<CampaignOrderResponsePage> getCampaignOrderInPage(Specification<CampaignOrderEntity> specification,
//																  Pageable pageable) {
//		return campaignOrderService.getCampaignOrderInPage(specification, pageable)
//			.map(campaignOrderMapper::domainToUserResponsePage);
//	}
//
//	@Override
//	public UserUserCampaignOrderDetailResponse getCampaignOrderById(Long id, Long userId) {
//		var userOrder = campaignOrderService.getOrderByIdAndUserId(id, userId);
//		return campaignOrderMapper.domainToUserDetailResponse(userOrder);
//	}
}
