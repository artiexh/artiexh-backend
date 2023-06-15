package com.artiexh.model.rest.cart.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
	@NotNull(message = "productId is required")
	private Long productId;
	@NotNull(message = "quantity is required")
	@Min(value = 0, message = "quantity must be greater than or equal 0")
	private Integer quantity;
}
