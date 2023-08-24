package com.artiexh.model.rest.order.request;

import com.artiexh.model.domain.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

	@NotNull
	private Long addressId;

	@NotNull
	private PaymentMethod paymentMethod;

	@NotEmpty
	@Valid
	private Set<CheckoutShop> shops;

}
