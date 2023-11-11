package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
	private ProductInventory productInventory;
	private CampaignSale campaignSale;
	private Money price;
	private Long quantity;
	private Long soldQuantity;
}
