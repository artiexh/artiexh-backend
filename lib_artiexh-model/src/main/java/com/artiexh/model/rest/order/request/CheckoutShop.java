package com.artiexh.model.rest.order.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutShop {

	@NotNull
	private Long shopId;

	private String note;

	@NotEmpty
	@Valid
	private Set<CheckoutItem> items;

}
