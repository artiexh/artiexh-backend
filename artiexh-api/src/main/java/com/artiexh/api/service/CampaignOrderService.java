package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.CampaignOrderEntity;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.model.domain.CampaignOrder;
import com.artiexh.model.rest.order.admin.response.AdminCampaignOrderResponse;
import com.artiexh.model.rest.order.admin.response.AdminCampaignOrderResponsePage;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import com.artiexh.model.rest.order.request.UpdateShippingOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import reactor.core.publisher.Mono;

public interface CampaignOrderService {

	Page<CampaignOrder> getCampaignOrderInPage(Specification<CampaignOrderEntity> query, Pageable pageable);

	CampaignOrder getCampaignOrderById(Long orderId);

	Page<AdminCampaignOrderResponsePage> getAdminCampaignOrderInPage(Specification<CampaignOrderEntity> query, Pageable pageable);

	AdminCampaignOrderResponse getAdminCampaignOrderById(Long orderId);

	CampaignOrder getOrderByIdAndOwner(Long orderId, Long artistId);

	CampaignOrder getOrderByIdAndUserId(Long orderId, Long userId);

	Mono<ShipFeeResponse.ShipFee> getShippingFee(Long userId, GetShippingFeeRequest request);

	AdminCampaignOrderResponse updateShippingOrderStatus(Long orderId,
														 UpdateShippingOrderRequest updateShippingOrderRequest);

	void cancelOrder(Long orderId, String message, Long updatedBy);

	void refundOrder(Long orderId, Long createdBy);

}
