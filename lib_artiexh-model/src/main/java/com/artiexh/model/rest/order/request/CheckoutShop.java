package com.artiexh.model.rest.order.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class CheckoutShop {

	@NotNull
	private Long shopId;

	private String note;

	@NotEmpty
	private Set<CheckoutItem> items;

}
