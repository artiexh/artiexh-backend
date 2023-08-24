package com.artiexh.model.rest.order.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutItem {

	@NotNull
	private Long productId;

	@Min(1)
	private Integer quantity;

}
