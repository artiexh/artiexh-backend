package com.artiexh.model.rest.order.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
	private Set<OrderDetailResponse> orderDetails;
}
