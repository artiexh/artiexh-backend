package com.artiexh.model.rest.marketplace.salecampaign.request;

import com.artiexh.model.domain.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductInSaleRequest {
	private Integer quantity;
	private Money price;
	private BigDecimal artistProfit;
}
