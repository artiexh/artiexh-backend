package com.artiexh.model.rest.order.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
	private String paymentUrl;
}
