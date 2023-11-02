package com.artiexh.model.rest.cart.response;

import com.artiexh.model.rest.campaign.response.CampaignResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartCampaignResponse {
	private CampaignResponse campaign;
	private Set<CartItemResponse> items;
}
