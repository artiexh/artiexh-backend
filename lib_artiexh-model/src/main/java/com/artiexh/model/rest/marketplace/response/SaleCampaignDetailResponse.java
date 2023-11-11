package com.artiexh.model.rest.marketplace.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleCampaignDetailResponse extends SaleCampaignResponse {
	private String content;
	private Set<ProductInSaleCampaignResponse> products;
}
