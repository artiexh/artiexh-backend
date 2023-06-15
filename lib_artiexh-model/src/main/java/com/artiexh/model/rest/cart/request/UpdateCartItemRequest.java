package com.artiexh.model.rest.cart.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequest {
	@NotNull(message = "items is required")
	private Set<CartItemRequest> items;
}
