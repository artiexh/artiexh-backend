package com.artiexh.model.rest.user;

import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.domain.PaymentMethod;
import com.artiexh.model.domain.Shop;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderResponsePage {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private Shop shop;

	private String note;

	private PaymentMethod paymentMethod;

	private OrderStatus status;

	private Instant modifiedDate;

	private Instant createdDate;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long orderId;
}
