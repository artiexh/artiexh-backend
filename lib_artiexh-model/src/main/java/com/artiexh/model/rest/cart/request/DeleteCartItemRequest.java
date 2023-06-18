package com.artiexh.model.rest.cart.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCartItemRequest {

	@NotNull(message = "productIds is required")
	private Set<Long> productIds;

}
