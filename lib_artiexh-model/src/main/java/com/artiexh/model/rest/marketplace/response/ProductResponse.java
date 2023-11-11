package com.artiexh.model.rest.marketplace.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse extends ProductInSaleCampaignResponse {
	private SaleCampaignResponse saleCampaign;
}
