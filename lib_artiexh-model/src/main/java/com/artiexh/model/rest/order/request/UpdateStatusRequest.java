package com.artiexh.model.rest.order.request;

import com.artiexh.model.domain.OrderStatus;
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
	private OrderStatus status;
}
