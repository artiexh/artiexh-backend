package com.artiexh.model.rest.cart.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
	Set<CartCampaignResponse> campaigns;
}
