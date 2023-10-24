package com.artiexh.model.rest.order.request;

import com.artiexh.model.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusRequest {
	private String message;
	@NotNull
	private OrderStatus status;
}
