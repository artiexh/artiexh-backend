package com.artiexh.model.rest.user;

import com.artiexh.model.domain.UserAddress;
import com.artiexh.model.rest.transaction.OrderTransactionResponse;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderGroupResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private Set<UserOrderResponse> orders;
	private UserAddress shippingAddress;
	private OrderTransactionResponse currentTransaction;
}
