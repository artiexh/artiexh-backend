package com.artiexh.model.rest.artist;

import com.artiexh.model.domain.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopOrderResponsePage {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private User user;

	private UserAddress shippingAddress;

	private String note;

	private PaymentMethod paymentMethod;

	private OrderStatus status;

	private LocalDateTime modifiedDate;

	private LocalDateTime createdDate;

	private Set<OrderHistory> orderHistories;
}
