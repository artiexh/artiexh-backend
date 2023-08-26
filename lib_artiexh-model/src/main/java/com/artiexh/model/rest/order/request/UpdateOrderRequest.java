package com.artiexh.model.rest.order.request;

import com.artiexh.model.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
	@NotNull
	private OrderStatus status;
}
