package com.artiexh.model.rest.order.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailUserOrderResponse extends UserOrderResponse {
	private Set<UserCampaignOrderResponse> campaignOrders;
}
