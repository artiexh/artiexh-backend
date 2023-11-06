package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.CampaignOrderEntity;
import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.rest.order.user.response.UserCampaignOrderResponse;
import com.artiexh.model.rest.order.user.response.UserCampaignOrderResponsePage;
import com.artiexh.model.rest.order.user.response.UserOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {

	UserOrderResponse getOrderById(Long id, Long userId);

	Page<UserOrderResponse> getOrderInPage(Specification<OrderEntity> specification, Pageable pageable);

	Page<UserCampaignOrderResponsePage> getCampaignOrderInPage(Specification<CampaignOrderEntity> specification,
															   Pageable pageable);

	UserCampaignOrderResponse getCampaignOrderById(Long id, Long userId);

}
