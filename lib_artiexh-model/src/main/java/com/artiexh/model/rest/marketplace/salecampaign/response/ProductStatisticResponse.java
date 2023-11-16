package com.artiexh.model.rest.marketplace.salecampaign.response;

import com.artiexh.model.domain.Money;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatisticResponse {
	private String productCode;
	private Long soldQuantity;
	private Long quantity;
	private String name;
	private Money revenue;
}
