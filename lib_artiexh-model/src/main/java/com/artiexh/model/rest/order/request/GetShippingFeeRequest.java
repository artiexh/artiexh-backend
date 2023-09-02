package com.artiexh.model.rest.order.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetShippingFeeRequest {

	@NotNull
	private Long addressId;

	@NotNull
	private Long shopId;

	@NotNull
	private Integer totalWeight;

}
