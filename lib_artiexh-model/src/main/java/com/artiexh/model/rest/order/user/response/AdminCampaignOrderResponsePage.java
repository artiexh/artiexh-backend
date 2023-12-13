package com.artiexh.model.rest.order.user.response;

import com.artiexh.model.rest.campaign.response.CampaignResponse;
import com.artiexh.model.rest.order.response.OrderDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminCampaignOrderResponsePage extends CampaignOrderResponsePage {
	private CampaignResponse.Owner user;
	private Set<OrderDetailResponse> orderDetails;
	private BigDecimal totalPrice;
}
